package io.camunda.zeebe.spring.client.config;

import static org.assertj.core.api.Assertions.*;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.configuration.ZeebeClientConfiguration;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.grpc.ClientInterceptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

public class ZeebeClientConfigurationTest {
  private static ZeebeClientConfiguration configuration(
      ZeebeClientConfigurationProperties legacyProperties,
      CamundaClientProperties properties,
      Authentication authentication,
      JsonMapper jsonMapper,
      List<ClientInterceptor> interceptors,
      ZeebeClientExecutorService executorService) {
    return new ZeebeClientConfiguration(
        legacyProperties, properties, authentication, jsonMapper, interceptors, executorService);
  }

  private static ZeebeClientConfigurationProperties legacyProperties() {
    return new ZeebeClientConfigurationProperties(new MockEnvironment());
  }

  private static CamundaClientProperties properties() {
    return new CamundaClientProperties();
  }

  private static Authentication authentication() {
    return new Authentication() {
      @Override
      public Map<String, String> getTokenHeader(Product product) {
        return null;
      }

      @Override
      public void resetToken(Product product) {}
    };
  }

  private static JsonMapper jsonMapper() {
    return new ZeebeObjectMapper();
  }

  private static ZeebeClientExecutorService executorService() {
    return ZeebeClientExecutorService.createDefault();
  }

  @Test
  void shouldCreateSingletonCredentialProvider() {
    ZeebeClientConfiguration configuration =
        configuration(
            legacyProperties(),
            properties(),
            authentication(),
            jsonMapper(),
            Collections.emptyList(),
            executorService());
    CredentialsProvider credentialsProvider1 = configuration.getCredentialsProvider();
    CredentialsProvider credentialsProvider2 = configuration.getCredentialsProvider();
    assertThat(credentialsProvider1).isSameAs(credentialsProvider2);
  }
}
