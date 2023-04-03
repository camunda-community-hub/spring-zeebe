package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class OutboundConnectorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final JobWorkerManager jobWorkerManager;
  private final OutboundConnectorFactory connectorFactory;
  private final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers;
  private Map<String, ZeebeWorkerValue> zeebeWorkerValuesByName = new HashMap<>();

  public OutboundConnectorManager(
    JobWorkerManager jobWorkerManager,
    OutboundConnectorFactory connectorFactory,
    List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers
  ) {
    this.jobWorkerManager = jobWorkerManager;
    this.connectorFactory = connectorFactory;
    this.zeebeWorkerValueCustomizers = zeebeWorkerValueCustomizers;
  }

  public void start(final ZeebeClient client) {
    // Currently, existing Spring beans have a higher priority
    // One result is that you will not disable Spring Bean Connectors by providing environment variables for a specific connector
    Set<OutboundConnectorConfiguration> outboundConnectors =
      new TreeSet<>(new OutboundConnectorConfigurationComparator());

    outboundConnectors.addAll(connectorFactory.getConfigurations());

    outboundConnectors.stream().forEach(connector -> openWorkerForOutboundConnector(client, connector));
  }

  public void openWorkerForOutboundConnector(ZeebeClient client, OutboundConnectorConfiguration connector) {
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue()
      .setName(connector.getName())
      .setType(connector.getType())
      .setFetchVariables(connector.getInputVariables())
      .setAutoComplete(true);
    zeebeWorkerValueCustomizers.forEach(customizer -> customizer.customize(zeebeWorkerValue));

    zeebeWorkerValuesByName.put(zeebeWorkerValue.getName(), zeebeWorkerValue);

    if (zeebeWorkerValue.getEnabled()==null || zeebeWorkerValue.getEnabled()==Boolean.TRUE) {
      jobWorkerManager.openWorker(
        client,
        zeebeWorkerValue,
        connector);
    }
  }

  public void stop(ZeebeClient client) {
    jobWorkerManager.closeAllOpenWorkers();
  }

  public ZeebeWorkerValue getZeebeWorkerValue(String name) {
    return zeebeWorkerValuesByName.get(name);
  }

}
