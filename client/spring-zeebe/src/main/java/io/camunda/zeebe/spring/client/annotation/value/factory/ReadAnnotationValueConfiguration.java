package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.zeebe.spring.client.properties.ZeebeClientProperties;
import io.camunda.zeebe.spring.util.ZeebeExpressionResolver;
import org.springframework.context.annotation.Bean;

/**
 * Bean-Definitions for annotation attribute processing.
 */
public class ReadAnnotationValueConfiguration {

  @Bean
  public ZeebeExpressionResolver zeebeExpressionResolver() {
    return new ZeebeExpressionResolver();
  }

  @Bean
  public ReadZeebeDeploymentValue readZeebeDeploymentValue(final ZeebeExpressionResolver resolver) {
    return new ReadZeebeDeploymentValue(resolver);
  }

  @Bean
  public ReadZeebeWorkerValue readZeebeWorkerValue(final ZeebeExpressionResolver resolver, final ZeebeClientProperties zeebeClientProperties) {
    return new ReadZeebeWorkerValue(resolver, zeebeClientProperties);
  }

}
