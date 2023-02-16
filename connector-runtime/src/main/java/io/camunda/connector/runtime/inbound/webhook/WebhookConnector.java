package io.camunda.connector.runtime.inbound.webhook;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@InboundConnector(name = "WEBHOOK", type = WebhookConnector.TYPE_WEBHOOK)
@Component
@ConditionalOnProperty("camunda.connector.webhook.enabled")
public class WebhookConnector implements InboundConnectorExecutable {

  public static final String TYPE_WEBHOOK = "io.camunda.webhook:1";

  private final Logger LOG = LoggerFactory.getLogger(WebhookConnector.class);

  private final Map<String, Set<InboundConnectorProperties>> activeEndpoints = new HashMap<>();

  public boolean containsContextPath(String context) {
    return activeEndpoints.containsKey(context);
  }

  public List<WebhookConnectorProperties> getWebhookConnectorByContextPath(String context) {
    return activeEndpoints.get(context).stream()
      .map(WebhookConnectorProperties::new)
      .collect(Collectors.toList());
  }

  @Override
  public void activate(InboundConnectorProperties inboundConnectorProperties,
    InboundConnectorContext inboundConnectorContext) {

    WebhookConnectorProperties webhookProperties =
      new WebhookConnectorProperties(inboundConnectorProperties);

    activeEndpoints.compute(
      webhookProperties.getContext(),
      (context, endpoints) -> {
        if (endpoints == null) {
          Set<InboundConnectorProperties> newEndpoints = new HashSet<>();
          newEndpoints.add(inboundConnectorProperties);
          return newEndpoints;
        }
        endpoints.add(inboundConnectorProperties);
        return endpoints;
      });
  }

  @Override
  public void deactivate(InboundConnectorProperties inboundConnectorProperties) {
    WebhookConnectorProperties webhookProperties =
      new WebhookConnectorProperties(inboundConnectorProperties);

    activeEndpoints.compute(
      webhookProperties.getContext(),
      (context, endpoints) -> {
        if (endpoints == null || !endpoints.contains(inboundConnectorProperties)) {
          LOG.warn("Attempted to disable non-existing webhook endpoint. "
            + "This indicates a potential error in the connector lifecycle.");
          return endpoints;
        }
        endpoints.remove(inboundConnectorProperties);
        return endpoints;
      });
  }
}
