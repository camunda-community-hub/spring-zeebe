package io.camunda.common.auth;

public class SaaSAuthenticationBuilder {

  SaaSAuthentication saaSAuthentication;

  SaaSAuthenticationBuilder() {
    saaSAuthentication = new SaaSAuthentication();
  }

  public SaaSAuthenticationBuilder jwtConfig(JwtConfig jwtConfig) {
    saaSAuthentication.setJwtConfig(jwtConfig);
    return this;
  }

  public Authentication build() {
    return saaSAuthentication.build();
  }
}
