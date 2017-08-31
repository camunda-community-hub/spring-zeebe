package io.zeebe.spring.client.config;

import io.zeebe.client.TasksClient;
import io.zeebe.client.event.TaskEvent;
import io.zeebe.client.task.TaskHandler;
import io.zeebe.client.task.TaskSubscriptionBuilder;
import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.event.ClientStartedEvent;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public class SubscriptionBuilderPostProcessor implements BeanPostProcessor, Ordered {


    public SubscriptionBuilderPostProcessor(SpringZeebeClient client) {
        this.client = client;
    }

    @Value
    @AllArgsConstructor
    private static class ListenerMethod {

        private Object bean;
        private String beanName;
        private Method method;
        private ZeebeTaskListener annotation;



        public TaskSubscriptionBuilder taskSubscriptionBuilder(SpringZeebeClient client) {
            return  client.tasks().newTaskSubscription(annotation.topicName())
                    .lockOwner(annotation.lockOwner())
                    .handler(new TaskHandler() {
                        @Override
                        public void handle(TasksClient tasksClient, TaskEvent taskEvent) {
                            try {
                                method.invoke(bean, tasksClient, taskEvent);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    })
                    .lockTime(annotation.lockTime())
                    .taskFetchSize(annotation.taskFetchSize())
                    .taskType(annotation.taskType());
        }

    }

    private final List<ListenerMethod> listenerMethods = new ArrayList<>();


    private final SpringZeebeClient client;


    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        ReflectionUtils.doWithMethods(targetClass, method -> {
            ZeebeTaskListener annotation = AnnotationUtils.findAnnotation(method, ZeebeTaskListener.class);
            if (annotation != null) {
                ListenerMethod listenerMethod = new ListenerMethod(bean, beanName, method, annotation);
                listenerMethods.add(listenerMethod);
                log.info("processing afterInit: {}", listenerMethod);
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);

        return bean;
    }

    @EventListener
    void register(ClientStartedEvent __) {
        listenerMethods.stream().map(lm -> lm.taskSubscriptionBuilder(client)).forEach(TaskSubscriptionBuilder::open);
    }


    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
