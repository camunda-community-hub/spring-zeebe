package io.camunda.connector.runtime.inbound.webhook;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;

@InboundConnector(name = "WEBHOOK", type = WebhookConnectorRegistry.TYPE_WEBHOOK)
public class WebhookConnectorExecutable implements InboundConnectorExecutable {

  private final WebhookConnectorRegistry registry;
  private InboundConnectorProperties properties;

  public WebhookConnectorExecutable(WebhookConnectorRegistry registry) {
    this.registry = registry;
  }
  @Override
  public void activate(InboundConnectorContext inboundConnectorContext) {
    properties = inboundConnectorContext.getProperties();
    registry.activateEndpoint(inboundConnectorContext);
  }

  @Override
  public void deactivate() {
    registry.deactivateEndpoint(properties);
  }
}
