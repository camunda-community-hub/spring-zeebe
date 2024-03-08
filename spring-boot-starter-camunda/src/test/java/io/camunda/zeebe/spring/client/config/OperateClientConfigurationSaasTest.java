package io.camunda.zeebe.spring.client.config;

import static org.assertj.core.api.Assertions.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import io.camunda.zeebe.spring.client.configuration.OperateClientConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {OperateClientConfiguration.class, AuthenticationConfiguration.class},
    properties = {
      "camunda.client.mode=saas",
      "camunda.client.cluster-id=12345",
      "camunda.client.region=bru-2",
      "camunda.client.auth.client-id=my-client-id",
      "camunda.client.auth.client-secret=my-client-secret"
    })
public class OperateClientConfigurationSaasTest {
  @Autowired CamundaOperateClient camundaOperateClient;

  @Test
  void shouldCreateClient() {
    assertThat(camundaOperateClient).isNotNull();
  }
}
