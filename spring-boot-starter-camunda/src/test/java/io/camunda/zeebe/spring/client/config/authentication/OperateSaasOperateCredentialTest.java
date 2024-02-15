package io.camunda.zeebe.spring.client.config.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.JwtCredential;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SaaSAuthentication;
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
      "zeebe.client.cloud.region=syd-1",
      "zeebe.client.cloud.clusterId=cluster-id",
      "zeebe.client.cloud.clientId=client-id",
      "zeebe.client.cloud.clientSecret=client-secret",
      "camunda.operate.client.enabled=true",
      "camunda.operate.client.client-id=operate-client-id",
      "camunda.operate.client.client-secret=operate-client-secret"
    })
@ContextConfiguration(classes = OperateSaasOperateCredentialTest.TestConfig.class)
public class OperateSaasOperateCredentialTest {

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
    assertThat(authentication).isInstanceOf(SaaSAuthentication.class);
    assertThat(operateClient).isNotNull();
  }

  @Test
  public void testCredential() {
    SaaSAuthentication saaSAuthentication = (SaaSAuthentication) authentication;
    JwtCredential jwtCredential = saaSAuthentication.getJwtConfig().getProduct(Product.OPERATE);

    assertThat(jwtCredential.getClientId()).isEqualTo("operate-client-id");
    assertThat(jwtCredential.getClientSecret()).isEqualTo("operate-client-secret");
  }
}
