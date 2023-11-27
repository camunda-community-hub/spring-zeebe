package io.camunda.common.auth;

public class SelfManagedAuthenticationBuilder {

  SelfManagedAuthentication selfManagedAuthentication;

  SelfManagedAuthenticationBuilder() {
    selfManagedAuthentication = new SelfManagedAuthentication();
  }

  public SelfManagedAuthenticationBuilder jwtConfig(JwtConfig jwtConfig) {
    selfManagedAuthentication.setJwtConfig(jwtConfig);
    return this;
  }

  public SelfManagedAuthenticationBuilder keycloakUrl(String keycloakUrl) {
    selfManagedAuthentication.setKeycloakUrl(keycloakUrl);
    return this;
  }

  public SelfManagedAuthenticationBuilder keycloakRealm(String keycloakRealm) {
    if (keycloakRealm != null) {
      selfManagedAuthentication.setKeycloakRealm(keycloakRealm);
    }
    return this;
  }

  public Authentication build() {
    return selfManagedAuthentication.build();
  }
}
