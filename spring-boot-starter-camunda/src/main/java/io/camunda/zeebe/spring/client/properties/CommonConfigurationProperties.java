package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.properties.common.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "common")
@Deprecated
public class CommonConfigurationProperties extends Client {

  @Override
  public String toString() {
    return "CommonConfigurationProperties{" + "keycloak=" + keycloak + "} " + super.toString();
  }

  @NestedConfigurationProperty private Keycloak keycloak = new Keycloak();

  @DeprecatedConfigurationProperty(
      replacement = "not required",
      reason = "Please use 'camunda.client.auth'")
  @Deprecated
  public Keycloak getKeycloak() {
    return keycloak;
  }

  public void setKeycloak(Keycloak keycloak) {
    this.keycloak = keycloak;
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "not required")
  @Deprecated
  public Boolean getEnabled() {
    return super.getEnabled();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "not required")
  @Deprecated
  public String getBaseUrl() {
    return super.getBaseUrl();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "not required")
  @Deprecated
  public String getUrl() {
    return super.getUrl();
  }
}
