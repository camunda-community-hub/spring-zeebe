package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.properties.common.Client;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@ConfigurationProperties(prefix = "console.client")
@Deprecated
public class ConsoleClientConfigurationProperties extends Client {
  @Override
  @DeprecatedConfigurationProperty(replacement = "not implemented")
  @Deprecated
  public Boolean getEnabled() {
    return super.getEnabled();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "not implemented")
  @Deprecated
  public String getBaseUrl() {
    return super.getBaseUrl();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "not implemented")
  @Deprecated
  public String getUrl() {
    return super.getUrl();
  }
}
