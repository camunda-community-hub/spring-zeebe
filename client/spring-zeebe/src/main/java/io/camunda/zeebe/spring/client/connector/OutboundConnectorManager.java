package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorRegistrationHelper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OutboundConnectorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Set<OutboundConnectorConfiguration> outboundConnectors = new TreeSet<>(new OutboundConnectorConfigurationComparator());
  private final JobWorkerManager jobWorkerManager;

  public OutboundConnectorManager(JobWorkerManager jobWorkerManager) {
    this.jobWorkerManager = jobWorkerManager;
  }

  public void addConnectorDefinition(OutboundConnectorConfiguration connector) {
    if (outboundConnectors.contains(connector)) {
      LOGGER.info("Duplicate configuration of outbound connector {}. Ignoring.", connector);
    } else {
      outboundConnectors.add(connector);
    }
  }

  public void start(final ZeebeClient client) {
    // Currently, existing Spring beans have a higher priority
    // One result is that you will not disable Spring Bean Connectors by providing environment variables for a specific connector
    loadClasspathOutboundConnectors();

    outboundConnectors.forEach( connector -> {
      openWorkerForOutboundConnector(client, connector);
    });
  }

  /**
   *  Load connectors that are configured via Environment Variable or SPI,
   *  which are probably not Spring beans and thus not processed by the annotation processor
   */
  public void loadClasspathOutboundConnectors() {
    LOGGER.info("Reading environment variables or parsing SPI to find connectors that are not Spring beans");
    List<OutboundConnectorConfiguration> outboundConnectors = OutboundConnectorRegistrationHelper.parse();
    if (outboundConnectors.isEmpty()) {
      LOGGER.warn("No outbound connectors configured or found in classpath");
    } else {
      LOGGER.info("Found outbound connectors via classpath that will be registered: " + outboundConnectors);
    }
    for (OutboundConnectorConfiguration connector : outboundConnectors) {
      addConnectorDefinition(connector);
    }
  }

  public void openWorkerForOutboundConnector(ZeebeClient client, OutboundConnectorConfiguration connector) {
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue()
      .setName(connector.getName())
      .setType(connector.getType())
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
