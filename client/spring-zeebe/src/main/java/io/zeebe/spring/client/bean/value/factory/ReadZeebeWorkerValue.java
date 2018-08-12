package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.annotation.ZeebeWorker;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.zeebe.spring.util.ZeebeExpressionResolver;
import java.util.Optional;

public class ReadZeebeWorkerValue
    extends ReadAnnotationValue<MethodInfo, ZeebeWorker, ZeebeWorkerValue> {

  public ReadZeebeWorkerValue(final ZeebeExpressionResolver resolver) {
    super(resolver, ZeebeWorker.class);
  }

  @Override
  public Optional<ZeebeWorkerValue> apply(final MethodInfo methodInfo) {
    return methodInfo
        .getAnnotation(annotationType)
        .map(
            annotation ->
                ZeebeWorkerValue.builder()
                    .beanInfo(methodInfo)
                    .topicName(resolver.resolve(annotation.topic()))
                    .jobType(resolver.resolve(annotation.taskType()))
                    .lockOwner(resolver.resolve(annotation.lockOwner()))
                    .lockTime(annotation.lockTime())
                    .jobFetchSize(annotation.taskFetchSize())
                    .build());
  }
}
