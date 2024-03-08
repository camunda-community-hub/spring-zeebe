package io.camunda.zeebe.spring.client.properties.common;

import io.camunda.identity.sdk.IdentityConfiguration;
import io.camunda.identity.sdk.IdentityConfiguration.Type;
import jakarta.annotation.PostConstruct;

public class AuthProperties {

  // simple
  private String username;
  private String password;

  // oidc and saas
  private String clientId;
  private String clientSecret;

  private IdentityConfiguration.Type oidcType;
  private String issuer;

  public Type getOidcType() {
    return oidcType;
  }

  public void setOidcType(Type oidcType) {
    this.oidcType = oidcType;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }



  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }
}
