package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.bean.value.ZeebeTaskListenerValue;
import io.zeebe.spring.client.util.ZeebeExpressionResolver;

import java.util.Optional;

public class ReadZeebeTaskListenerValue extends ReadAnnotationValue<MethodInfo, ZeebeTaskListener, ZeebeTaskListenerValue>
{

    public ReadZeebeTaskListenerValue(final ZeebeExpressionResolver resolver)
    {
        super(resolver, ZeebeTaskListener.class);
    }

    @Override
    public Optional<ZeebeTaskListenerValue> apply(final MethodInfo methodInfo)
    {
        return methodInfo.getAnnotation(annotationType).map(annotation ->
                ZeebeTaskListenerValue.builder()
                        .beanInfo(methodInfo)
                        .topicName(resolver.resolve(annotation.topic()))
                        .taskType(resolver.resolve(annotation.taskType()))
                        .lockOwner(resolver.resolve(annotation.lockOwner()))
                        .lockTime(annotation.lockTime())
                        .taskFetchSize(annotation.taskFetchSize())
                        .build()
        );
    }
}
