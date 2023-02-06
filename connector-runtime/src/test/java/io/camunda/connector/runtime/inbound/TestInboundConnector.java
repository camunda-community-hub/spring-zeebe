package io.camunda.connector.runtime.inbound;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;

@InboundConnector(name = "TEST_INBOUND", type = "io.camunda:test-inbound:1")
public class TestInboundConnector implements InboundConnectorExecutable {

  @Override
  public void activate(InboundConnectorProperties inboundConnectorProperties,
    InboundConnectorContext inboundConnectorContext) {

  }

  @Override
  public void deactivate(InboundConnectorProperties inboundConnectorProperties) {

  }
}
