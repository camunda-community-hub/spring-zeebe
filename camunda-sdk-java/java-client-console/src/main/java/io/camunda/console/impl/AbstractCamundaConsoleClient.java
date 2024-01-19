package io.camunda.console.impl;

import io.camunda.console.client.api.DefaultApi;

public abstract class AbstractCamundaConsoleClient {
  private final DefaultApi api;

  public AbstractCamundaConsoleClient(DefaultApi api) {
    this.api = api;
  }

  public AbstractCamundaConsoleClient(AbstractCamundaConsoleClient consoleClient) {
    this.api = consoleClient.api;
  }

  protected DefaultApi getApi() {
    return api;
  }
}
