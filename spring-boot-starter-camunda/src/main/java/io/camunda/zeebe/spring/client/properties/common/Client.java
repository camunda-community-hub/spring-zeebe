package io.camunda.zeebe.spring.client.properties.common;

import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@Deprecated
public class Client {

  private String clientId;
  private String clientSecret;
  private String username;
  private String password;
  private Boolean enabled = false;
  private String url;
  private String authUrl;
  private String baseUrl;

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.client-id")
  @Deprecated
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.client-secret")
  @Deprecated
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.username")
  @Deprecated
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.password")
  @Deprecated
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.issuer")
  @Deprecated
  public String getAuthUrl() {
    return authUrl;
  }

  public void setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
}
