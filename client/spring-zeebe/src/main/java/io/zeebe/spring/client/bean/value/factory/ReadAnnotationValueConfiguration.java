package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.util.ZeebeExpressionResolver;
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
  public ReadZeebeWorkerValue readZeebeWorkerValue(final ZeebeExpressionResolver resolver) {
    return new ReadZeebeWorkerValue(resolver);
  }

}
