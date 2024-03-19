package io.camunda.zeebe.spring.client.config.legacy.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SimpleAuthentication;
import io.camunda.common.auth.SimpleCredential;
import io.camunda.identity.autoconfigure.IdentityAutoConfiguration;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.zeebe.spring.client.configuration.CommonClientConfiguration;
import io.camunda.zeebe.spring.client.configuration.JsonMapperConfiguration;
import io.camunda.zeebe.spring.client.configuration.OperateClientConfiguration;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
    properties = {
      "zeebe.client.broker.gatewayAddress=localhost12345",
      "camunda.operate.client.url=http://localhost:8081",
      "camunda.operate.client.username=username",
      "camunda.operate.client.password=password"
    })
@ContextConfiguration(classes = OperateSelfManagedBasicTest.TestConfig.class)
public class OperateSelfManagedBasicTest {

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

  @ImportAutoConfiguration({
    CommonClientConfiguration.class,
    OperateClientConfiguration.class,
    IdentityAutoConfiguration.class,
    JsonMapperConfiguration.class
  })
  @EnableConfigurationProperties({ZeebeClientConfigurationProperties.class})
  public static class TestConfig {}
}
