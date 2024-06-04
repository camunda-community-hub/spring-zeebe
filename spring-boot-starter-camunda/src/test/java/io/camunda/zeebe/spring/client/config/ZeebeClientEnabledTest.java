package io.camunda.zeebe.spring.client.config;

import static org.assertj.core.api.Assertions.*;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import io.camunda.zeebe.spring.client.configuration.ZeebeClientProdAutoConfiguration;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
      ZeebeClientProdAutoConfiguration.class,
      ZeebeClientConfigurationProperties.class,
      CamundaClientProperties.class,
      AuthenticationConfiguration.class
    },
    properties = "camunda.client.mode=simple")
public class ZeebeClientEnabledTest {
  @Autowired(required = false)
  private ZeebeClient zeebeClient;

  @Test
  void shouldNotEnableZeebeClient() {
    assertThat(zeebeClient).isNotNull();
  }
}
