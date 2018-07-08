package io.zeebe.spring.client.properties;

import io.zeebe.client.ZeebeClientConfiguration;

public interface ZeebeClientProperties extends ZeebeClientConfiguration {

  default boolean isAutoStartup() {
    return true;
  }
}
