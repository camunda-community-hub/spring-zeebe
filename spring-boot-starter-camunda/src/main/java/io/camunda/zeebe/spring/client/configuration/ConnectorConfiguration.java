package io.camunda.zeebe.spring.client.configuration;

import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.config.ConnectorConfigurationUtil;
import io.camunda.connector.impl.config.ConnectorPropertyResolver;
import io.camunda.connector.runtime.util.outbound.DefaultOutboundConnectorFactory;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.spring.client.connector.OutboundConnectorManager;
import io.camunda.zeebe.spring.client.connector.SpringConnectorPropertyResolver;
import io.camunda.zeebe.spring.client.connector.SpringSecretProvider;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@ConditionalOnProperty(prefix = "zeebe.client", name = "enabled", havingValue = "true",  matchIfMissing = true)
public class ConnectorConfiguration {

  @Bean
  public OutboundConnectorFactory outboundConnectorFactory() {
    return new DefaultOutboundConnectorFactory();
  }

  @Bean
  public OutboundConnectorManager outboundConnectorManager(
    final JobWorkerManager jobWorkerManager,
    final OutboundConnectorFactory outboundConnectorFactory,
    final ZeebeClientConfigurationProperties zeebeClientConfigurationProperties) {

    return new OutboundConnectorManager(jobWorkerManager, outboundConnectorFactory,zeebeClientConfigurationProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public SecretProvider secretProvider(Environment env) {
    return new SpringSecretProvider(env);
  }

  @Bean
  public ConnectorPropertyResolver connectorPropertyResolver(Environment env) {
    SpringConnectorPropertyResolver springPropertyResolver = new SpringConnectorPropertyResolver(env);
    // Use static configuration mechanism, as connectors are independant of Spring
    ConnectorConfigurationUtil.setCustomPropertyResolver(springPropertyResolver);
    return springPropertyResolver;
  }

}
