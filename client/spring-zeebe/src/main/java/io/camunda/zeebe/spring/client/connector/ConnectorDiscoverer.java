package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.ConnectorDiscoveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ConnectorDiscoverer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private boolean loadedOutboundFromClasspath = false;
  private final Set<OutboundConnectorConfiguration> outboundConnectors = new TreeSet<>(new OutboundConnectorConfigurationComparator());
  private boolean loadedInboundFromClasspath = false;
  private final Set<InboundConnectorConfiguration> inboundConnectors = new TreeSet<>(new InboundConnectorConfigurationComparator());

  /** Outbound */

  /**
   *  Load connectors that are configured via Environment Variable or SPI,
   *  which are probably not Spring beans and thus not processed by the annotation processor
   */
  protected void loadOutboundConnectorsFromClasspath() {
    LOGGER.info("Reading environment variables or parsing SPI to find outbound connectors that are not Spring beans");
    List<OutboundConnectorConfiguration> connectors = ConnectorDiscoveryHelper.parseOutboundConnectors();
    if (connectors.isEmpty()) {
      LOGGER.warn("No outbound connectors configured or found in classpath");
    } else {
      LOGGER.info("Found outbound connectors via classpath that will be registered: " + connectors);
    }
    for (OutboundConnectorConfiguration connector : connectors) {
      addOutboundConnector(connector);
    }
  }

  /**
   * Add a connector definition if discovered
   * (used internally when doing classpath scanning, but can also be called externally,
   * for example when the right Spring bean is found)
   * @param connector
   */
  public void addOutboundConnector(OutboundConnectorConfiguration connector) {
    if (outboundConnectors.contains(connector)) {
      LOGGER.info("Duplicate configuration of outbound connector {}. Ignoring.", connector);
    } else {
      outboundConnectors.add(connector);
    }
  }

  protected Collection<OutboundConnectorConfiguration> outboundConnectors() {
    if (!loadedOutboundFromClasspath) {
      // load connectors from classpath lazy, which typically means load them after manually added connectors are registered (e.g. Spring Beans)
      loadOutboundConnectorsFromClasspath();
      loadedOutboundFromClasspath = true;
    }
    return outboundConnectors;
  }

  /** Inbound */

  /**
   *  Load connectors that are configured via Environment Variable or SPI,
   *  which are probably not Spring beans and thus not processed by the annotation processor
   */
  protected void loadInboundConnectorsFromClasspath() {
    LOGGER.info("Reading environment variables or parsing SPI to find inbound connectors that are not Spring beans");
    List<InboundConnectorConfiguration> connectors = ConnectorDiscoveryHelper.parseInboundConnectors();
    if (connectors.isEmpty()) {
      LOGGER.warn("No inbound connectors configured or found in classpath");
    } else {
      LOGGER.info("Found inbound connectors via classpath that will be registered: " + connectors);
    }
    for (InboundConnectorConfiguration connector : connectors) {
      addInboundConnector(connector);
    }
  }

  /**
   * Add a connector definition if discovered
   * (used internally when doing classpath scanning, but can also be called externally,
   * for example when the right Spring bean is found)
   * @param connector
   */
  public void addInboundConnector(InboundConnectorConfiguration connector) {
    if (inboundConnectors.contains(connector)) {
      LOGGER.info("Duplicate configuration of inbound connector {}. Ignoring.", connector);
    } else {
      inboundConnectors.add(connector);
    }
  }

  protected Collection<InboundConnectorConfiguration> inboundConnectors() {
    if (!loadedInboundFromClasspath) {
      // load connectors from classpath lazy, which typically means load them after manually added connectors are registered (e.g. Spring Beans)
      loadInboundConnectorsFromClasspath();
      loadedInboundFromClasspath = true;
    }
    return inboundConnectors;
  }

  public InboundConnectorConfiguration findInboundConnector(String connectorType) {
    if (connectorType==null) {
      return null;
    }
    for (InboundConnectorConfiguration config:inboundConnectors()) {
      if (connectorType.equalsIgnoreCase(config.getType())) {
        return config;
      }
    }
    return null;
  }
}
