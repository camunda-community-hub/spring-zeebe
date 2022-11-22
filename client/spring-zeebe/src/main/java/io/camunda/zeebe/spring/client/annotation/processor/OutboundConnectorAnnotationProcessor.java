package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorRegistrationHelper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.connector.OutboundConnectorManager;
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

  private final OutboundConnectorManager outboundConnectorManager;

  public OutboundConnectorAnnotationProcessor(final OutboundConnectorManager outboundConnectorManager) {
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
      outboundConnectorManager.addConnectorDefinition(connector);
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
