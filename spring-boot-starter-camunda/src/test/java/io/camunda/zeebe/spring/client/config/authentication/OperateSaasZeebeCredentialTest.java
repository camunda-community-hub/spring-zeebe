package io.camunda.zeebe.spring.client.config.authentication;

import io.camunda.common.auth.*;
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
    "zeebe.client.cloud.region=syd-1",
    "zeebe.client.cloud.clusterId=cluster-id",
    "zeebe.client.cloud.clientId=client-id",
    "zeebe.client.cloud.clientSecret=client-secret",
    "camunda.operate.client.enabled=true"
  }
)
@ContextConfiguration(classes = OperateSaasZeebeCredentialTest.TestConfig.class)
public class OperateSaasZeebeCredentialTest {

  @ImportAutoConfiguration({CommonClientConfiguration.class, OperateClientConfiguration.class})
  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {

  }

  @Autowired
  private Authentication authentication;

  @Autowired
  private CamundaOperateClient operateClient;

  @Test
  public void testAuthentication() {
    assertThat(authentication).isInstanceOf(SaaSAuthentication.class);
    assertThat(operateClient).isNotNull();
  }

  @Test
  public void testCredential() {
    SaaSAuthentication saaSAuthentication = (SaaSAuthentication) authentication;
    JwtCredential jwtCredential = saaSAuthentication.getJwtConfig().getProduct(Product.OPERATE);

    assertThat(jwtCredential.getClientId()).isEqualTo("client-id");
    assertThat(jwtCredential.getClientSecret()).isEqualTo("client-secret");
  }
}
