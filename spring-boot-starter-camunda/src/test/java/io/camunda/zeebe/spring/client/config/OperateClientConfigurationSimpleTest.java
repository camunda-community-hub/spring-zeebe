package io.camunda.zeebe.spring.client.config;

import static org.assertj.core.api.Assertions.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import io.camunda.zeebe.spring.client.configuration.OperateClientConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {OperateClientConfiguration.class, AuthenticationConfiguration.class})
@ActiveProfiles("camunda-simple")
public class OperateClientConfigurationSimpleTest {
  @Autowired CamundaOperateClient camundaOperateClient;

  @Test
  void shouldCreateClient() {
    assertThat(camundaOperateClient).isNotNull();
  }
}
