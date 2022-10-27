package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorRegistrationHelper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 */
public class OutboundConnectorAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Set<OutboundConnectorConfiguration> outboundConnectors = new TreeSet<>(new OutboundConnectorComparator());
  private final JobWorkerManager jobWorkerManager;
  private boolean nonSpringConnectorsLoaded = false;

  public OutboundConnectorAnnotationProcessor(final JobWorkerManager jobWorkerManager) {
    this.jobWorkerManager = jobWorkerManager;
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasClassAnnotation(OutboundConnector.class);
  }

  @Override
  public void configureFor(ClassInfo beanInfo) {
    Optional<OutboundConnector> annotation = beanInfo.getAnnotation(OutboundConnector.class);
    if (annotation.isPresent()) {
      OutboundConnectorConfiguration connector = new OutboundConnectorConfiguration()
            .setFunction((OutboundConnectorFunction) beanInfo.getBean())
            .setType(annotation.get().type())
            .setName(annotation.get().name())
            .setInputVariables(annotation.get().inputVariables());

      LOGGER.info("Configuring outbound connector {} of bean '{}'", connector, beanInfo.getBeanName());
      addConnectorDefinition(connector);
    }
  }

  public void addConnectorDefinition(OutboundConnectorConfiguration connector) {
    if (outboundConnectors.contains(connector)) {
      LOGGER.info("Duplicate configuration of outbound connector {}. Ignoring.", connector);
    } else {
      outboundConnectors.add(connector);
    }
  }

  @Override
  public void start(final ZeebeClient client) {
    if (!nonSpringConnectorsLoaded) {
      // Connectors can also be configured via Environment Variable or SPI
      // So they are probably not Spring beans that are processed by this annotation processor
      // load those as well before starting all workers
      // TODO: Currently, existing Spring beans have a higher priority - do we want that?
      // One result is that you will not disable Spring Bean Connectors by providing environment variables for a specific connector
      loadNonSpringOutboundConnectors();
      nonSpringConnectorsLoaded = true;
    }
    outboundConnectors.forEach( connector -> {
      openWorkerForOutboundConnector(client, connector);
      //LOGGER.info("Started worker {} for connector {}", jobWorker, connector);
    });
  }

  public void loadNonSpringOutboundConnectors() {
    LOGGER.info("Reading environment variables or parsing SPI to find connectors that are not Spring beans");
    List<OutboundConnectorConfiguration> outboundConnectors = OutboundConnectorRegistrationHelper.parse();
    if (outboundConnectors.isEmpty()) {
      LOGGER.warn(
        "No outbound connectors configured or found in current Connector Runtime's classpath");
    } else {
      LOGGER.info("Found connectors that will be registered: " + outboundConnectors);
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
      connector.getFunction());
  }

  @Override
  public void stop(ZeebeClient client) {
    jobWorkerManager.closeAllOpenWorkers();
  }

  private static class OutboundConnectorComparator implements Comparator<OutboundConnectorConfiguration> {

    @Override
    public int compare(OutboundConnectorConfiguration o1, OutboundConnectorConfiguration o2) {
      if (o1 == o1) return 0;
      if (o1 == null) return -1;

      return o1.getType().compareTo(o2.getType());
    }
  }
}
