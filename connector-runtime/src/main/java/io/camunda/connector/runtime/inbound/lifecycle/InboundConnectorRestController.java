package io.camunda.connector.runtime.inbound.lifecycle;

import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorProperties;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InboundConnectorRestController {

  private final InboundConnectorManager inboundManager;

  public InboundConnectorRestController(InboundConnectorManager inboundManager) {
    this.inboundManager = inboundManager;
  }

  @GetMapping("/inbound")
  public List<ActiveInboundConnectorResponse> getActiveInboundConnectors(
    @RequestParam(required = false) String type,
    @RequestParam(required = false) String bpmnProcessId) {

    var activeConnectors = inboundManager.getActiveConnectorsByBpmnId();
    var filteredByBpmnProcessId = filterByBpmnProcessId(activeConnectors, bpmnProcessId);
    var filteredByType = filterByConnectorType(filteredByBpmnProcessId, type);

    // TODO: replace this with a general solution
    // e.g. consider an optional method in InboundConnectorExecutable that returns data to be shown in Modeler
    return filteredByType.stream()
      .map(
        properties ->
          new ActiveInboundConnectorResponse(
            properties.getBpmnProcessId(), properties.getType(), extractPublicConnectorData(properties)))
      .collect(Collectors.toList());
  }

  private List<InboundConnectorProperties> filterByBpmnProcessId(
    Map<String, Set<InboundConnectorProperties>> connectors, String bpmnProcessId) {
    if (bpmnProcessId != null) {
      Set<InboundConnectorProperties> connectorsForBpmnId = connectors.get(bpmnProcessId);
      if (connectorsForBpmnId == null) {
        return Collections.emptyList();
      }
      return new ArrayList<>(connectors.get(bpmnProcessId));
    }
    return connectors.values().stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  private List<InboundConnectorProperties> filterByConnectorType(
    List<InboundConnectorProperties> properties, String type) {
    if (type == null) {
      return properties;
    }
    return properties.stream()
      .filter(props -> type.equals(props.getType()))
      .collect(Collectors.toList());
  }

  private Map<String, Object> extractPublicConnectorData(InboundConnectorProperties properties) {
    if (WebhookConnectorRegistry.TYPE_WEBHOOK.equals(properties.getType())) {
      WebhookConnectorProperties webhookProps = new WebhookConnectorProperties(properties);
      return Map.of("path", webhookProps.getContext());
    }
    return null;
  }
}
