package io.camunda.commons.auth;

public class SaaSAuthenticationBuilder {

  SaaSAuthentication saaSAuthentication;

  public

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
