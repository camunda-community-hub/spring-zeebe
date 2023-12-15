package io.camunda.zeebe.spring.client.configuration.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class OperateClientCondition extends AnyNestedCondition {
  public OperateClientCondition() {
    super(ConfigurationPhase.PARSE_CONFIGURATION);
  }

  @ConditionalOnProperty(name = "operate.client.client-id")
  static class ClientIdCondition { }

  @ConditionalOnProperty(name = "operate.client.username")
  static class UsernameCondition { }

  @ConditionalOnProperty(name = "operate.client.auth-url")
  static class AuthUrlCondition { }

  @ConditionalOnProperty(name = "operate.client.base-url")
  static class BaseUrlCondition { }
}
