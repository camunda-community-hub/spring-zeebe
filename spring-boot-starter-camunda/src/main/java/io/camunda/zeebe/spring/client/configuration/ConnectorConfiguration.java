package io.camunda.zeebe.spring.client.configuration;

import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.config.ConnectorConfigurationUtil;
import io.camunda.connector.impl.config.ConnectorPropertyResolver;
import io.camunda.connector.runtime.util.outbound.DefaultOutboundConnectorFactory;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;import io.camunda.zeebe.spring.client.connector.Filter.ZeebeConnectorFilter;import io.camunda.zeebe.spring.client.connector.OutboundConnectorManager;
import io.camunda.zeebe.spring.client.connector.SpringConnectorPropertyResolver;
import io.camunda.zeebe.spring.client.connector.SpringSecretProvider;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;import java.util.List;

@ConditionalOnProperty(prefix = "zeebe.client", name = {"enabled", "worker.connectors.enabled"}, havingValue = "true",  matchIfMissing = true)
public class ConnectorConfiguration {

@Bean
  public OutboundConnectorFactory outboundConnectorFactory() {
    return new DefaultOutboundConnectorFactory();
  }

  @Bean
  public OutboundConnectorManager outboundConnectorManager(
    final JobWorkerManager jobWorkerManager,
    final OutboundConnectorFactory outboundConnectorFactory,
    final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers) {

    return new OutboundConnectorManager(jobWorkerManager, outboundConnectorFactory, zeebeWorkerValueCustomizers);
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
