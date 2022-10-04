package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.connector.api.secret.SecretProvider;
import org.springframework.core.env.Environment;

/**
 * USes Spring {@link Environment} to resolve secrets
 * (will look into properties files as well as system properties)
 */
public class SpringSecretProvider implements SecretProvider {

  private final Environment environment;

  public SpringSecretProvider(Environment environment) {
    this.environment = environment;
  }

  @Override
  public String getSecret(String s) {
    return environment.getProperty(s);
  }
}
