package io.camunda.zeebe.spring.client.configuration.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class ZeebeClientCondition extends AllNestedConditions {
  public ZeebeClientCondition() {
    super(ConfigurationPhase.PARSE_CONFIGURATION);
  }

  @ConditionalOnProperty(name = "zeebe.client.enabled", matchIfMissing = true)
  static class LegacyCondition {}

  @ConditionalOnProperty(name = "camunda.client.zeebe.enabled", matchIfMissing = true)
  static class CurrentCondition {}
}
