package io.camunda.common.auth;

import io.camunda.common.auth.Authentication.AuthenticationBuilder;
import io.camunda.common.auth.identity.IdentityConfig;

public class SelfManagedAuthenticationBuilder implements AuthenticationBuilder {
  private IdentityConfig identityConfig;

  public SelfManagedAuthenticationBuilder withIdentityConfig(IdentityConfig identityConfig) {
    this.identityConfig = identityConfig;
    return this;
  }

  @Override
  public Authentication build() {
    return new SelfManagedAuthentication(identityConfig);
  }
}
