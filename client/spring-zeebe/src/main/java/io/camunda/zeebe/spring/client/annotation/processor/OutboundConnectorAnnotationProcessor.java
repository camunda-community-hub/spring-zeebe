package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.value.OutboundConnectorValue;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadOutboundConnectorValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.client.jobhandling.copy.OutboundConnectorFunctionInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.ReflectionUtils.doWithMethods;

/**
 */
public class OutboundConnectorAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private ReadOutboundConnectorValue reader;
  private final List<OutboundConnectorValue> outboundConnectors = new ArrayList<>();
  private final JobWorkerManager jobWorkerManager;

  public OutboundConnectorAnnotationProcessor(ReadOutboundConnectorValue reader,  final JobWorkerManager jobWorkerManager) {
    this.reader = reader;
    this.jobWorkerManager = jobWorkerManager;
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasClassAnnotation(OutboundConnector.class);
  }

  @Override
  public void configureFor(ClassInfo beanInfo) {
    Optional<OutboundConnectorValue> connectorValue = reader.apply(beanInfo);
    if (connectorValue.isPresent()) {
      OutboundConnectorValue connector = connectorValue.get();
      LOGGER.info("Configuring outbound connector {} of bean '{}'", connector, beanInfo.getBeanName());
      outboundConnectors.add(connector);
    }
  }

  @Override
  public void start(final ZeebeClient client) {
    outboundConnectors.forEach( connector -> {
      ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue()
        .setName(connector.getName())
        .setType(connector.getType())
        .setAutoComplete(true);
      JobWorker jobWorker = jobWorkerManager.openWorker(
        client,
        zeebeWorkerValue,
        (OutboundConnectorFunction) connector.getBeanInfo().getBean());
      //LOGGER.info("Started worker {} for connector {}", jobWorker, connector);
    });
  }

  @Override
  public void stop(ZeebeClient client) {
    jobWorkerManager.closeAllOpenWorkers();
  }
}
