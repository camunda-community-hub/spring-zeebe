package io.camunda.connector.runtime.inbound.webhook;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("camunda.connector.webhook.enabled")
public class WebhookConnectorRegistry {

  public static final String TYPE_WEBHOOK = "io.camunda:webhook:1";

  private final Logger LOG = LoggerFactory.getLogger(WebhookConnectorRegistry.class);

  // active endpoints grouped by context path (additionally indexed by correlationPointId for faster lookup)
  private final Map<String, Map<String, InboundConnectorContext>> activeEndpointsByContext =
    new HashMap<>();

  public boolean containsContextPath(String context) {
    return activeEndpointsByContext.containsKey(context) &&
      !activeEndpointsByContext.get(context).isEmpty();
  }

  public List<InboundConnectorContext> getWebhookConnectorByContextPath(String context) {
    return new ArrayList<>(activeEndpointsByContext.get(context).values());
  }

  public void activateEndpoint(InboundConnectorContext connectorContext) {

    InboundConnectorProperties properties = connectorContext.getProperties();
    WebhookConnectorProperties webhookProperties = new WebhookConnectorProperties(properties);

    activeEndpointsByContext.compute(
      webhookProperties.getContext(),
      (context, endpoints) -> {
        if (endpoints == null) {
          Map<String, InboundConnectorContext> newEndpoints = new HashMap<>();
          newEndpoints.put(properties.getCorrelationPointId(), connectorContext);
          return newEndpoints;
        }
        endpoints.put(properties.getCorrelationPointId(), connectorContext);
        return endpoints;
      });
  }

  public void deactivateEndpoint(InboundConnectorProperties inboundConnectorProperties) {
    WebhookConnectorProperties webhookProperties =
      new WebhookConnectorProperties(inboundConnectorProperties);

    activeEndpointsByContext.compute(
      webhookProperties.getContext(),
      (context, endpoints) -> {
        if (endpoints == null || !endpoints.containsKey(inboundConnectorProperties.getCorrelationPointId())) {
          LOG.warn("Attempted to disable non-existing webhook endpoint. "
            + "This indicates a potential error in the connector lifecycle.");
          return endpoints;
        }
        endpoints.remove(inboundConnectorProperties.getCorrelationPointId());
        return endpoints;
      });
  }
}
