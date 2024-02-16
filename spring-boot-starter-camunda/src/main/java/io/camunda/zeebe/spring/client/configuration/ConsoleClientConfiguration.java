package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.console.CamundaConsoleClient;
import io.camunda.console.CamundaConsoleClientBuilder;
import io.camunda.zeebe.spring.client.properties.ConsoleClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(
    prefix = "console.client",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false)
@EnableConfigurationProperties(ConsoleClientConfigurationProperties.class)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
public class ConsoleClientConfiguration {

  private final Authentication authentication;

  @Autowired
  public ConsoleClientConfiguration(Authentication authentication) {
    this.authentication = authentication;
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaConsoleClient camundaConsoleClient(
      ConsoleClientConfigurationProperties properties) {
    return new CamundaConsoleClientBuilder()
        .authentication(authentication)
        .consoleUrl(properties.getBaseUrl())
        .build();
  }
}
