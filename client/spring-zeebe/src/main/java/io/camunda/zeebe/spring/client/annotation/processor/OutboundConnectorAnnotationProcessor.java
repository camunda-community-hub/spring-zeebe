package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.connector.ConnectorDiscoverer;
import io.camunda.zeebe.spring.client.connector.OutboundConnectorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

/**
 */
public class OutboundConnectorAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ConnectorDiscoverer connectorDiscoverer;
  private final OutboundConnectorManager outboundConnectorManager;

  public OutboundConnectorAnnotationProcessor(final ConnectorDiscoverer connectorDiscoverer, OutboundConnectorManager outboundConnectorManager) {
    this.connectorDiscoverer = connectorDiscoverer;
    this.outboundConnectorManager = outboundConnectorManager;
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
      connectorDiscoverer.addOutboundConnector(connector);
    }
  }

  @Override
  public void start(final ZeebeClient client) {
    outboundConnectorManager.start(client);
  }

  @Override
  public void stop(ZeebeClient client) {
    outboundConnectorManager.stop(client);
  }

}
