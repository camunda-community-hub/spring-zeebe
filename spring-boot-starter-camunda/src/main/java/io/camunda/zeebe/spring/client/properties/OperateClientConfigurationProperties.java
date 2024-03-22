package io.camunda.zeebe.spring.client.properties;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@ConfigurationProperties(prefix = "camunda.operate.client")
@Deprecated
public class OperateClientConfigurationProperties {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${zeebe.client.cloud.cluster-id:#{null}}")
  private String clusterId;

  @Value("${zeebe.client.cloud.region:bru-2}")
  private String region;

  private String clientId;
  private String clientSecret;
  private String username;
  private String password;
  private Boolean enabled = false;
  private String url;

  private String keycloakUrl;
  private String keycloakRealm = "camunda-platform";

  private String keycloakTokenUrl;

  private String baseUrl;

  private String authUrl;

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

  @DeprecatedConfigurationProperty(replacement = "camunda.client.operate.enabled")
  @Deprecated
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.operate.base-url")
  @Deprecated
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @DeprecatedConfigurationProperty(
      replacement = "camunda.client.auth.issuer",
      reason =
          "There is no more specific keycloak config, the issuer is ${keycloakUrl}/auth/realms/${keycloakRealm}")
  @Deprecated
  public String getKeycloakUrl() {
    return keycloakUrl;
  }

  public void setKeycloakUrl(String keycloakUrl) {
    this.keycloakUrl = keycloakUrl;
  }

  @DeprecatedConfigurationProperty(
      replacement = "camunda.client.auth.issuer",
      reason =
          "There is no more specific keycloak config, the issuer is ${keycloakUrl}/auth/realms/${keycloakRealm}")
  @Deprecated
  public String getKeycloakRealm() {
    return keycloakRealm;
  }

  public void setKeycloakRealm(String keycloakRealm) {
    this.keycloakRealm = keycloakRealm;
  }

  @DeprecatedConfigurationProperty(
      replacement = "camunda.client.auth.issuer",
      reason =
          "There is no more specific keycloak config, the issuer is ${keycloakUrl}/auth/realms/${keycloakRealm}")
  @Deprecated
  public String getKeycloakTokenUrl() {
    return keycloakTokenUrl;
  }

  public void setKeycloakTokenUrl(String keycloakTokenUrl) {
    this.keycloakTokenUrl = keycloakTokenUrl;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.operate.base-url")
  @Deprecated
  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.issuer")
  @Deprecated
  public String getAuthUrl() {
    return authUrl;
  }

  public void setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.operate.base-url")
  @Deprecated
  public String getOperateUrl() {
    if (url != null) {
      LOG.debug("Connecting to Camunda Operate on URL: " + url);
      return url;
    } else if (clusterId != null) {
      String url = "https://" + region + "." + getFinalBaseUrl() + "/" + clusterId + "/";
      LOG.debug("Connecting to Camunda Operate SaaS via URL: " + url);
      return url;
    }
    throw new IllegalArgumentException(
        "In order to connect to Camunda Operate you need to specify either a SaaS clusterId or an Operate URL.");
  }

  private String getFinalBaseUrl() {
    if (getBaseUrl() != null) {
      return getBaseUrl();
    } else {
      return "operate.camunda.io";
    }
  }
}
