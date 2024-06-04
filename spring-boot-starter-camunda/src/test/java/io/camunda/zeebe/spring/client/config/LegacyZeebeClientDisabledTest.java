package io.camunda.zeebe.spring.client.config;

import static org.assertj.core.api.Assertions.*;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.configuration.ZeebeClientProdAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = ZeebeClientProdAutoConfiguration.class,
    properties = "zeebe.client.enabled=false")
public class LegacyZeebeClientDisabledTest {
  @Autowired(required = false)
  private ZeebeClient zeebeClient;

  @Test
  void shouldNotEnableZeebeClient() {
    assertThat(zeebeClient).isNull();
  }
}
