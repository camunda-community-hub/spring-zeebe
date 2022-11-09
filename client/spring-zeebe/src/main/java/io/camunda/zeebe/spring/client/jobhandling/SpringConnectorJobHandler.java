package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.runtime.util.outbound.ConnectorJobHandler;

public class SpringConnectorJobHandler extends ConnectorJobHandler {

  private SecretProvider secretProvider;

  public SpringConnectorJobHandler(OutboundConnectorFunction call, SecretProvider secretProvider) {
    super(call);
    this.secretProvider = secretProvider;
  }

  @Override
  protected SecretProvider getSecretProvider() {
    if (secretProvider == null) {
      secretProvider = super.getSecretProvider();
    }
    return secretProvider;
  }
}
