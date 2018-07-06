package io.zeebe.spring.client.config.processor;

import io.zeebe.client.ZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeTaskListenerValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeTaskListenerValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.util.ReflectionUtils.doWithMethods;

/**
 * Triggered by {@link SubscriptionBuilderPostProcessor#postProcessAfterInitialization(Object, String)} to
 * add Handler subscriptions for {@link ZeebeTaskListener} method-annotations.
 */
@Slf4j
public class TaskHandlerPostProcessor extends BeanInfoPostProcessor
{

    private final ReadZeebeTaskListenerValue reader;

    public TaskHandlerPostProcessor(final ReadZeebeTaskListenerValue reader)
    {
        this.reader = reader;
    }

    @Override
    public boolean test(final ClassInfo beanInfo)
    {
        return beanInfo.hasMethodAnnotation(ZeebeTaskListener.class);
    }

    @Override
    public Consumer<ZeebeClient> apply(final ClassInfo beanInfo)
    {
        log.info("taskhandling: {}", beanInfo);

        final List<ZeebeTaskListenerValue> annotatedMethods = new ArrayList<>();

        doWithMethods(
            beanInfo.getTargetClass(),
            method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
            ReflectionUtils.USER_DECLARED_METHODS
        );

        return client -> annotatedMethods.forEach(m -> {
            client.topicClient(m.getTopicName())
                  .jobClient()
                  .newWorker()
                  .jobType(m.getTaskType())
                  .handler((jobClient, job) -> m.getBeanInfo().invoke(jobClient, job))
                  .name(m.getLockOwner())
                  .timeout(m.getLockTime())
                  .bufferSize(m.getTaskFetchSize())
                  .open();
            log.info("register taskHandler: {}", m);
        });
    }

}

