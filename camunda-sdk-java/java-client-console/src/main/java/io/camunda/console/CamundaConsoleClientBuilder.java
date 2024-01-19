package io.camunda.console;

import io.camunda.common.auth.Authentication;

public class CamundaConsoleClientBuilder {
  private Authentication authentication;
  private String consoleUrl;

  public CamundaConsoleClientBuilder authentication(Authentication authentication) {
    this.authentication = authentication;
    return this;
  }

  public CamundaConsoleClientBuilder consoleUrl(String consoleUrl) {
    this.consoleUrl = consoleUrl;
    return this;
  }

  public CamundaConsoleClient build() {
    return CamundaConsoleClient.create(authentication, consoleUrl);
  }
}
