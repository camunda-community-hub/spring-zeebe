package io.camunda.zeebe.spring.client.config.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.common.auth.*;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.zeebe.spring.client.configuration.CommonClientConfiguration;
import io.camunda.zeebe.spring.client.configuration.OperateClientConfiguration;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
    properties = {
      "zeebe.client.broker.gatewayAddress=localhost12345",
      "zeebe.authorization.server.url=http://zeebe-authorization-server",
      "zeebe.client.id=client-id",
      "zeebe.client.secret=client-secret",
      "zeebe.token.audience=sample-audience",
      "camunda.operate.client.url=http://localhost:8081",
      "camunda.operate.client.username=username",
      "camunda.operate.client.password=password"
    })
@ContextConfiguration(classes = OperateSelfManagedBasicTest.TestConfig.class)
public class OperateSelfManagedBasicTest {

  @ImportAutoConfiguration({CommonClientConfiguration.class, OperateClientConfiguration.class})
  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {
    @Bean
    public JsonMapper jsonMapper() {
      return new SdkObjectMapper();
    }
  }

  @Autowired private Authentication authentication;

  @Autowired private CamundaOperateClient operateClient;

  @Test
  public void testAuthentication() {
    assertThat(authentication).isInstanceOf(SimpleAuthentication.class);
    assertThat(operateClient).isNotNull();
  }

  @Test
  public void testCredential() {
    SimpleAuthentication simpleAuthentication = (SimpleAuthentication) authentication;
    SimpleCredential simpleCredential =
        simpleAuthentication.getSimpleConfig().getProduct(Product.OPERATE);

    assertThat(simpleCredential.getUser()).isEqualTo("username");
    assertThat(simpleCredential.getPassword()).isEqualTo("password");
  }
}
