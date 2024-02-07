package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class PropertyBasedZeebeWorkerValueCustomizerTest {
  @Test
  void shouldApplyOverrides(){
    ZeebeClientConfigurationProperties properties = new ZeebeClientConfigurationProperties(null);
    properties.applyOverrides();
    properties.getWorker().getOverride().put("someWorker",new ZeebeWorkerValue().enabled(false));
    PropertyBasedZeebeWorkerValueCustomizer customizer = new PropertyBasedZeebeWorkerValueCustomizer(properties);
    ZeebeWorkerValue original = new ZeebeWorkerValue().type("someWorker").enabled(true);
    customizer.customize(original);
    assertThat(original.getEnabled()).isEqualTo(false);
  }
}
