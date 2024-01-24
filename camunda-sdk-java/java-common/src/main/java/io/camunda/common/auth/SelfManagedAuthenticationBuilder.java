package io.camunda.common.auth;

import io.camunda.common.auth.identity.IdentityConfig;

public class SelfManagedAuthenticationBuilder {

  SelfManagedAuthentication selfManagedAuthentication;

  SelfManagedAuthenticationBuilder() {
    selfManagedAuthentication = new SelfManagedAuthentication();
  }

  public SelfManagedAuthenticationBuilder jwtConfig(JwtConfig jwtConfig) {
    selfManagedAuthentication.setJwtConfig(jwtConfig);
    return this;
  }

  public SelfManagedAuthenticationBuilder identityConfig(IdentityConfig identityConfig) {
    selfManagedAuthentication.setIdentityConfig(identityConfig);
    return this;
  }

  public Authentication build() {
    return selfManagedAuthentication.build();
  }
}
