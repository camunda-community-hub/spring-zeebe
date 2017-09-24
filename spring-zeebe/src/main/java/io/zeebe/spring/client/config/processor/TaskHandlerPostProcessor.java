package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.bean.ZeebeTaskListenerValue;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.util.ReflectionUtils.doWithMethods;

/**
 * Triggered by {@link SubscriptionBuilderPostProcessor#postProcessAfterInitialization(Object, String)} to
 * add Handler subscriptions for {@link ZeebeTaskListener} method-annotations.
 */
@Slf4j
public class TaskHandlerPostProcessor extends BeanInfoPostProcessor<MethodInfo, ZeebeTaskListener, ZeebeTaskListenerValue> {

    @Override
    public boolean test(final ClassInfo beanInfo) {
        return beanInfo.hasMethodAnnotation(ZeebeTaskListener.class);
    }

    @Override
    public Consumer<SpringZeebeClient> apply(final ClassInfo beanInfo) {
        log.info("taskhandling: {}", beanInfo);

        final List<ZeebeTaskListenerValue> annotatedMethods = new ArrayList<>();

        doWithMethods(
                beanInfo.getTargetClass(),
                method -> create(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
                ReflectionUtils.USER_DECLARED_METHODS
        );

        return client -> annotatedMethods.forEach(m -> {
            client.tasks().newTaskSubscription(m.getTopicName())
                    .lockOwner(m.getLockOwner())
                    .handler((tasksClient, taskEvent) -> m.getBeanInfo().invoke(tasksClient, taskEvent))
                    .lockTime(m.getLockTime())
                    .taskFetchSize(m.getTaskFetchSize())
                    .taskType(m.getTaskType())
                    .open();
            log.info("register taskHandler: {}", m);
        });
    }

    @Override
    public Class<ZeebeTaskListener> annotationType() {
        return ZeebeTaskListener.class;
    }

    @Override
    public Optional<ZeebeTaskListenerValue> create(MethodInfo beanInfo) {
        return beanInfo.getAnnotation(annotationType()).map(annotation ->
                ZeebeTaskListenerValue.builder()
                        .beanInfo(beanInfo)
                        .topicName(resolver.resolve(annotation.topicName()))
                        .taskType(resolver.resolve(annotation.taskType()))
                        .lockOwner(resolver.resolve(annotation.lockOwner()))
                        .lockTime(annotation.lockTime())
                        .taskFetchSize(annotation.taskFetchSize())
                        .build()
        );
    }
}

