package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.properties.common.Client;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@ConfigurationProperties(prefix = "tasklist.client")
@Deprecated
public class TasklistClientConfigurationProperties extends Client {

  @Override
  @DeprecatedConfigurationProperty(replacement = "camunda.client.tasklist.enabled")
  @Deprecated
  public Boolean getEnabled() {
    return super.getEnabled();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "camunda.client.tasklist.base-url")
  @Deprecated
  public String getBaseUrl() {
    return super.getBaseUrl();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "camunda.client.tasklist.base-url")
  @Deprecated
  public String getUrl() {
    return super.getUrl();
  }
}
