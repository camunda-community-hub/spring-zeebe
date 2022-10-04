package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.zeebe.client.ZeebeClientConfiguration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
  public ReadZeebeWorkerValue readZeebeWorkerValue(Environment environment) { // can#t use @Value because it is only evaluated after constructors are executed
    String defaultWorkerType = environment.getProperty("zeebe.client.worker.default-type", (String)null);
    String defaultJobWorkerName = environment.getProperty("zeebe.client.worker.default-name", (String)null);
    return new ReadZeebeWorkerValue(defaultWorkerType, defaultJobWorkerName);
  }

  @Bean
  public ReadOutboundConnectorValue readOutboundConnectorValue() {
    return new ReadOutboundConnectorValue();
  }

}
