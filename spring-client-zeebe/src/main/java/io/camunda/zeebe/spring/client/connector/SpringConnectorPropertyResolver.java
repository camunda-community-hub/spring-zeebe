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
    if (environment.containsProperty(key)) {
      return true;
    } else if (environment.containsProperty(createSpringFormattedKey(key))) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String getProperty(String key) {
    if (environment.containsProperty(key)) {
      return environment.getProperty(key);
    }
    // Check if maybe a ENV_VARIABLE_FORMAT was provided - lowercase it:
    String alternativeKey = createSpringFormattedKey(key);
    if (environment.containsProperty(alternativeKey)) {
      return environment.getProperty(alternativeKey);
    }
    return null;
  }

  private String createSpringFormattedKey(String key) {
    return key.toLowerCase().replaceAll("_", ".");
  }
}
