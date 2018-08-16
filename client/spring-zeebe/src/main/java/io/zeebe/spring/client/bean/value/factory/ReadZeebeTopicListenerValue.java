package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.bean.value.ZeebeTopicListenerValue;
import io.zeebe.spring.util.ZeebeExpressionResolver;
import java.util.Optional;

public class ReadZeebeTopicListenerValue
  extends ReadAnnotationValue<MethodInfo, ZeebeTopicListener, ZeebeTopicListenerValue> {

  public ReadZeebeTopicListenerValue(final ZeebeExpressionResolver resolver) {
    super(resolver, ZeebeTopicListener.class);
  }

  @Override
  public Optional<ZeebeTopicListenerValue> apply(final MethodInfo methodInfo) {
    return methodInfo
      .getAnnotation(annotationType)
      .map(
        annotation ->
          ZeebeTopicListenerValue.builder()
            .beanInfo(methodInfo)
            .name(resolver.resolve(annotation.name()))
            .topic(resolver.resolve(annotation.topic()))
            .build());
  }
}
