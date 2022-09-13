package io.camunda.zeebe.spring.client.annotation.value.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Bean-Definitions for annotation attribute processing.
 */
public class ReadAnnotationValueConfiguration {

  @Value("#{ systemProperties['user.region'] }")
  private String defaultLocale;

  @Bean
  public ReadZeebeDeploymentValue readZeebeDeploymentValue() {
    return new ReadZeebeDeploymentValue();
  }

  @Bean
  public ReadZeebeWorkerValue readZeebeWorkerValue(@Value("${zeebe.client.worker.default-type:#{null}}") String defaultWorkerType) {
    return new ReadZeebeWorkerValue(defaultWorkerType);
  }

}
