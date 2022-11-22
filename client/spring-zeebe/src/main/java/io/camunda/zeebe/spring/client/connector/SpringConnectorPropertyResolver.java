package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.config.ConnectorPropertyResolver;
import org.springframework.core.env.Environment;

public class SpringConnectorPropertyResolver implements ConnectorPropertyResolver {

  private final Environment environment;

  public SpringConnectorPropertyResolver(Environment environment) {
    this.environment = environment;
  }

  @Override
  public boolean containsProperty(String key) {
    return environment.containsProperty(key);
  }

  @Override
  public String getProperty(String key) {
    return environment.getProperty(key);
  }
}
