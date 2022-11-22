package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.config.ConnectorBeanFactory;
import io.camunda.connector.impl.config.ConnectorConfigurationUtil;
import io.camunda.connector.impl.config.ConnectorPropertyResolver;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class ConnectorConfiguration {

  @Bean
  public OutboundConnectorManager outboundConnectorManager(final JobWorkerManager jobWorkerManager) {
    return new OutboundConnectorManager(jobWorkerManager);
  }

  @Bean
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

  @Bean
  public ConnectorBeanFactory connectorBeanFactory(ApplicationContext context) {
    SpringConnectorBeanFactory springConnectorBeanFactory = new SpringConnectorBeanFactory(context);
    ConnectorConfigurationUtil.setCustomBeanFactory(springConnectorBeanFactory);
    return springConnectorBeanFactory;
  }

}
