package io.camunda.common.auth;

import io.camunda.common.auth.Authentication.AuthenticationBuilder;

public class SimpleAuthenticationBuilder implements AuthenticationBuilder {
  private SimpleConfig simpleConfig;

  public SimpleAuthenticationBuilder withSimpleConfig(SimpleConfig simpleConfig) {
    this.simpleConfig = simpleConfig;
    return this;
  }

  @Override
  public Authentication build() {
    return new SimpleAuthentication(simpleConfig);
  }
}
