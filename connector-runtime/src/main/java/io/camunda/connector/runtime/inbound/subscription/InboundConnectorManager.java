package io.camunda.connector.runtime.inbound.subscription;

import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.runtime.util.inbound.DefaultInboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorProperties;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.connector.ConnectorDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Service
public class InboundConnectorManager {

  private static final Logger LOG = LoggerFactory.getLogger(InboundConnectorManager.class);

  private final ConnectorDiscoverer connectorDiscoverer;
  private final InboundConnectorContext inboundConnectorContext;

  private Map<String, InboundConnectorConfiguration> activeConnectorsByType = new HashMap<>();

  @Autowired
  public InboundConnectorManager(ConnectorDiscoverer connectorDiscoverer, InboundConnectorContext inboundConnectorContext) {
    this.connectorDiscoverer = connectorDiscoverer;
    this.inboundConnectorContext = inboundConnectorContext;
  }

  public void foundInboundConnector(InboundConnectorProperties connectorProperties) {
    // try to find a matching connector for provided type
    String connectorType = connectorProperties.getType();

    if (activeConnectorsByType.containsKey(connectorType)) {
      LOG.trace("Subscription connector {1} already activated, ignoring", connectorType);
      return;
    }

    InboundConnectorConfiguration inboundConnectorConfig = connectorDiscoverer.findInboundConnector(connectorType);
    if (inboundConnectorConfig==null) {
      // TODO: Not sure if an exception makes too much sense here?
      throw new RuntimeException("Cannot find inbound connector for type '"+connectorType+"' as requested by process with id " + connectorProperties.getBpmnProcessId());
    }

    try {
      // TODO: This is currently nailed to Subscription - needs to be extended once we have Polling too
      inboundConnectorConfig.getConnector().activate(connectorProperties, inboundConnectorContext);
      activeConnectorsByType.put(connectorType, inboundConnectorConfig);
    } catch (Exception ex) {
      LOG.error("Could not activate inbound connector {1} because of exception {2}", connectorProperties, ex.getMessage(), ex);
    }
    // Now all known connectors on the classpath need to be known
    // Somehow the type of the connector must resolve to either a
    //PollingInboundConnectorFunction function1 = null;
    //SubscriptionInboundConnector connector = null;
    // Then this runtime will either start a Subscription or some polling component

  }

  @PreDestroy // TODO: Is this a good lifecycle hook?
  public void stop() {
    for (InboundConnectorConfiguration connectorConfig:activeConnectorsByType.values()) {
      try {
        connectorConfig.getConnector().deactivate();
      } catch (Exception ex) {
        LOG.error("Could not deactivate inbound connector {1} because of exception {2}", connectorConfig, ex.getMessage(), ex);
      }
    }
  }
}
