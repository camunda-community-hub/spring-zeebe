package io.camunda.zeebe.spring.client.config.authentication;

import io.camunda.common.auth.*;
import io.camunda.identity.autoconfigure.IdentityAutoConfiguration;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.zeebe.spring.client.configuration.CommonClientConfiguration;
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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
  properties = {
    "zeebe.client.broker.gatewayAddress=localhost12345",
    "zeebe.authorization.server.url=http://zeebe-authorization-server",
    "zeebe.client.id=client-id",
    "zeebe.client.secret=client-secret",
    "zeebe.token.audience=sample-audience",
    "camunda.operate.client.keycloak-token-url=https://local-keycloak/auth/realms/camunda-platform/protocol/openid-connect/token",
    "camunda.operate.client.url=http://localhost:8081"
  }
)
@ContextConfiguration(classes = OperateSelfManagedKeycloakTokenUrlTest.TestConfig.class)
public class OperateSelfManagedKeycloakTokenUrlTest {

  @ImportAutoConfiguration({CommonClientConfiguration.class, OperateClientConfiguration.class, IdentityAutoConfiguration.class})
  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {

  }

  @Autowired
  private Authentication authentication;

  @Autowired
  private CamundaOperateClient operateClient;

  @Test
  public void testAuthentication() {
    assertThat(authentication).isInstanceOf(SelfManagedAuthentication.class);
    assertThat(operateClient).isNotNull();
  }

  @Test
  public void testCredential() {
    SelfManagedAuthentication selfManagedAuthentication = (SelfManagedAuthentication) authentication;
    JwtCredential jwtCredential = selfManagedAuthentication.getJwtConfig().getProduct(Product.OPERATE);

    assertThat(jwtCredential.getClientId()).isEqualTo("client-id");
    assertThat(jwtCredential.getClientSecret()).isEqualTo("client-secret");
  }

}
