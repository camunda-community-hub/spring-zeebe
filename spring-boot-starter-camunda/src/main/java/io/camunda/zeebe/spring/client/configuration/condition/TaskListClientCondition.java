package io.camunda.zeebe.spring.client.configuration.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class TaskListClientCondition extends AnyNestedCondition {

  public TaskListClientCondition() {
    super(ConfigurationPhase.PARSE_CONFIGURATION);
  }

  @ConditionalOnProperty(name = "camunda.tasklist.client.client-id")
  static class ClientIdCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.username")
  static class UsernameCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.auth-url")
  static class AuthUrlCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.base-url")
  static class BaseUrlCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.keycloak-url")
  static class KeycloakUrlCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.keycloak-token-url")
  static class KeycloakTokenUrlCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.url")
  static class UrlCondition { }

  @ConditionalOnProperty(name = "camunda.tasklist.client.enabled")
  static class EnableCondition { }

}
