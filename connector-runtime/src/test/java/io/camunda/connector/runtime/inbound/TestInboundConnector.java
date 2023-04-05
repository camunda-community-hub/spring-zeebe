package io.camunda.connector.runtime.inbound;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;

@InboundConnector(name = "TEST_INBOUND", type = "io.camunda:test-inbound:1")
public class TestInboundConnector implements InboundConnectorExecutable {

  private InboundConnectorContext context;

  @Override
  public void activate(InboundConnectorContext context) {
    this.context = context;
  }

  @Override
  public void deactivate() {

  }

  public InboundConnectorContext getProvidedContext() {
    if (context == null) {
      throw new IllegalStateException("Connector has not been activated yet. No context available.");
    }
    return context;
  }
}
