package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.config.ConnectorConfigurationUtil;
import io.camunda.connector.impl.config.ConnectorPropertyResolver;
import io.camunda.connector.runtime.util.outbound.DefaultOutboundConnectorFactory;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ConnectorConfiguration {

  @Bean
  public OutboundConnectorFactory outboundConnectorFactory() {
    return new DefaultOutboundConnectorFactory();
  }

  @Bean
  public OutboundConnectorManager outboundConnectorManager(
    final JobWorkerManager jobWorkerManager,
    final OutboundConnectorFactory outboundConnectorFactory) {

    return new OutboundConnectorManager(jobWorkerManager, outboundConnectorFactory);
  }

  public static class OnMissingSecretProvider implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return context.getBeanFactory().getBeanNamesForType(SecretProvider.class).length<=0;
    }
  }

  @Bean
  @Conditional(value=OnMissingSecretProvider.class)
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
