package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.connector.OutboundConnectorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

/**
 */
public class OutboundConnectorAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final OutboundConnectorManager outboundConnectorManager;
  private final OutboundConnectorFactory outboundConnectorFactory;

  public OutboundConnectorAnnotationProcessor(
    final OutboundConnectorManager outboundConnectorManager,
    final OutboundConnectorFactory outboundConnectorFactory) {
    this.outboundConnectorManager = outboundConnectorManager;
    this.outboundConnectorFactory = outboundConnectorFactory;
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasClassAnnotation(OutboundConnector.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void configureFor(ClassInfo beanInfo) {
    Optional<OutboundConnector> annotation = beanInfo.getAnnotation(OutboundConnector.class);
    if (annotation.isPresent()) {
      OutboundConnectorConfiguration connector = new OutboundConnectorConfiguration(
        annotation.get().name(),
        annotation.get().inputVariables(),
        annotation.get().type(),
        (Class<? extends OutboundConnectorFunction>) beanInfo.getTargetClass());

      LOGGER.info("Configuring outbound connector {} of bean '{}'", connector, beanInfo.getBeanName());
      outboundConnectorFactory.registerConfiguration(connector);
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
