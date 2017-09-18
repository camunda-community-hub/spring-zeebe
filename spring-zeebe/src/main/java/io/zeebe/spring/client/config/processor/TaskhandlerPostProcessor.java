package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.doWithMethods;

@Slf4j
public class TaskhandlerPostProcessor extends BeanInfoPostProcessor {

    @Override
    public boolean test(final ClassInfo beanInfo) {
        return beanInfo.hasMethodAnnotation(ZeebeTaskListener.class);
    }

    @Override
    public Consumer<SpringZeebeClient> apply(final ClassInfo beanInfo) {
        log.info("taskhandling: {}", beanInfo);

        final List<ZeebeTaskListener.Annotated> annotatedMethods = new ArrayList<>();

        doWithMethods(
                beanInfo.getTargetClass(),
                method -> Optional.ofNullable(findAnnotation(method, ZeebeTaskListener.class))
                                .ifPresent(a -> annotatedMethods.add(ZeebeTaskListener.Annotated.of(beanInfo.toMethodInfo(method), a))),
                ReflectionUtils.USER_DECLARED_METHODS
        );

        return client -> annotatedMethods.forEach(m -> {
            log.info("register taskHandler: {}", m);
            client.tasks().newTaskSubscription(m.getTopicName())
                    .lockOwner(m.getLockOwner())
                    .handler((tasksClient, taskEvent) -> m.getBeanInfo().invoke( tasksClient, taskEvent))
                    .lockTime(m.getLockTime())
                    .taskFetchSize(m.getTaskFetchSize())
                    .taskType(m.getTaskType())
                    .open();
        });
    }
}
