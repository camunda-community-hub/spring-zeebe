package io.camunda.zeebe.spring.client.properties.common;

public class AuthProperties {
  // mode
  private AuthMode mode;
  // simple
  private String username;
  private String password;
  // oidc
  private String authUrl;
  private String scope;
  private String audience;
  // oidc and saas
  private String clientId;
  private String clientSecret;
  // saas
  private String clusterId;

  public AuthMode getMode() {
    return mode;
  }

  public void setMode(AuthMode mode) {
    this.mode = mode;
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

  public String getAuthUrl() {
    return authUrl;
  }

  public void setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }

  public enum AuthMode {
    SIMPLE, OIDC, SAAS
  }
}
