package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.TreeSet;

public class OutboundConnectorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final JobWorkerManager jobWorkerManager;

  private final OutboundConnectorFactory connectorFactory;

  public OutboundConnectorManager(
    JobWorkerManager jobWorkerManager,
    OutboundConnectorFactory connectorFactory
  ) {
    this.jobWorkerManager = jobWorkerManager;
    this.connectorFactory = connectorFactory;
  }

  public void start(final ZeebeClient client) {
    // Currently, existing Spring beans have a higher priority
    // One result is that you will not disable Spring Bean Connectors by providing environment variables for a specific connector

    Set<OutboundConnectorConfiguration> outboundConnectors =
      new TreeSet<>(new OutboundConnectorConfigurationComparator());

    outboundConnectors.addAll(connectorFactory.getConfigurations());
    outboundConnectors.forEach(connector -> openWorkerForOutboundConnector(client, connector));
  }

  public void openWorkerForOutboundConnector(ZeebeClient client, OutboundConnectorConfiguration connector) {
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue()
      .setName(connector.getName())
      .setType(connector.getType())
      .setFetchVariables(connector.getInputVariables())
      .setAutoComplete(true);
    jobWorkerManager.openWorker(
      client,
      zeebeWorkerValue,
      connector);
  }

  public void stop(ZeebeClient client) {
    jobWorkerManager.closeAllOpenWorkers();
  }
}
