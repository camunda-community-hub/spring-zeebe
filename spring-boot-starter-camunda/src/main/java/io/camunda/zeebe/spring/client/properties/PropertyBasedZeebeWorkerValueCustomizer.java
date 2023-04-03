package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;

import java.util.Map;

public class PropertyBasedZeebeWorkerValueCustomizer implements ZeebeWorkerValueCustomizer {

  private final ZeebeClientConfigurationProperties zeebeClientConfigurationProperties;

  public PropertyBasedZeebeWorkerValueCustomizer(final ZeebeClientConfigurationProperties zeebeClientConfigurationProperties) {
    this.zeebeClientConfigurationProperties = zeebeClientConfigurationProperties;
  }

  @Override
  public void customize(ZeebeWorkerValue zeebeWorker) {
    ZeebeWorkerValue zeebeWorkerValue = zeebeClientConfigurationProperties.getWorker().getOverrideByType(zeebeWorker.getType());
    if (zeebeWorkerValue!=null) {
      zeebeWorker.merge(zeebeWorkerValue);
    }
  }
}
