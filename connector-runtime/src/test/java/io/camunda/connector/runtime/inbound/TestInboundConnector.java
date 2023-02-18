package io.camunda.connector.runtime.inbound;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;

@InboundConnector(name = "TEST_INBOUND", type = "io.camunda:test-inbound:1")
public class TestInboundConnector implements InboundConnectorExecutable {

  @Override
  public void activate(InboundConnectorContext context) {

  }

  @Override
  public void deactivate() {

  }
}
