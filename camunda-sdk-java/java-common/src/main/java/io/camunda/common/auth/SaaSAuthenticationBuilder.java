package io.camunda.common.auth;

public class SaaSAuthenticationBuilder {

  SaaSAuthentication saaSAuthentication;

  SaaSAuthenticationBuilder() {
    saaSAuthentication = new SaaSAuthentication();
  }

  public SaaSAuthenticationBuilder jwtConfig(JwtConfig jwtConfig) {
    saaSAuthentication.jwtConfig(jwtConfig);
    return this;
  }

  public Authentication build() {
    return saaSAuthentication.build();
  }

}
