package io.camunda.zeebe.spring.client.annotation.value.factory;

import org.springframework.context.annotation.Bean;

/**
 * Bean-Definitions for annotation attribute processing.
 */
public class ReadAnnotationValueConfiguration {

  @Bean
  public ReadZeebeDeploymentValue readZeebeDeploymentValue() {
    return new ReadZeebeDeploymentValue();
  }

  @Bean
  public ReadZeebeWorkerValue readZeebeWorkerValue() {
    return new ReadZeebeWorkerValue();
  }

}
