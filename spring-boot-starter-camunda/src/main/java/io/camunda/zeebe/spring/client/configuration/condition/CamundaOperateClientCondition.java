package io.camunda.zeebe.spring.client.configuration.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * This will be deprecated once we move to the new schema (i.e. not prefixing with camunda.*)
 */
@Deprecated
public class CamundaOperateClientCondition extends AnyNestedCondition {
  public CamundaOperateClientCondition() {
    super(ConfigurationPhase.PARSE_CONFIGURATION);
  }

  @ConditionalOnProperty(name = "camunda.operate.client.client-id")
  static class ClientIdCondition { }

  @ConditionalOnProperty(name = "camunda.operate.client.username")
  static class UsernameCondition { }

  @ConditionalOnProperty(name = "camunda.operate.client.auth-url")
  static class AuthUrlCondition { }

  @ConditionalOnProperty(name = "camunda.operate.client.base-url")
  static class BaseUrlCondition { }
}
