package io.camunda.zeebe.spring.client.metrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MetricsDefaultConfiguration {

  public static class OnMissingMetricsRecorder implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return context.getBeanFactory().getBeanNamesForType(MetricsRecorder.class).length<=0;
    }
  }

  @Bean
  @Conditional(value=OnMissingMetricsRecorder.class)
  public MetricsRecorder noopMetricsRecorder() {
    return new DefaultNoopMetricsRecorder();
  }
}
