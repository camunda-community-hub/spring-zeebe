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
    final Map<String, ZeebeWorkerValue> workerConfigurationMap = zeebeClientConfigurationProperties.getWorker().getOverride();
    final String workerType = zeebeWorker.getType();
    if (workerConfigurationMap.containsKey(workerType)) {
      final ZeebeWorkerValue zeebeWorkerValue = workerConfigurationMap.get(workerType);
      zeebeWorker.merge(zeebeWorkerValue);
    }
  }
}
