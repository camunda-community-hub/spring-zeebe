package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ContainerProperties;
import io.camunda.zeebe.process.test.extension.testcontainer.ContainerizedEngine;
import io.camunda.zeebe.process.test.extension.testcontainer.EngineContainer;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import io.camunda.zeebe.spring.test.proxy.ZeebeClientProxy;
import io.camunda.zeebe.spring.test.proxy.ZeebeTestEngineProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.invoke.MethodHandles;

/**
 * Test execution listener binding the Zeebe engine to current test context.
 */
public class ZeebeTestExecutionListener implements TestExecutionListener, Ordered {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ContainerizedEngine containerizedEngine;
  private ZeebeClient zeebeClient;

  public void beforeTestClass(@NonNull TestContext testContext) {
    LOGGER.info("Creating Zeebe Testcontainer...");

    final EngineContainer container = EngineContainer.getContainer();
    container.start();
    containerizedEngine = new ContainerizedEngine(
        container.getHost(),
        container.getMappedPort(ContainerProperties.getContainerPort()),
        container.getMappedPort(ContainerProperties.getGatewayPort()));

    LOGGER.info("...finished creating Zeebe Testcontainer");
  }

  public void beforeTestMethod(@NonNull TestContext testContext) {
    LOGGER.info("Create Zeebe Testcontainer engine");
    containerizedEngine.start();
    testContext.getApplicationContext().getBean(ZeebeTestEngineProxy.class).swapZeebeEngine(containerizedEngine);

    final RecordStream recordStream = RecordStream.of(containerizedEngine.getRecordStreamSource());
    BpmnAssert.initRecordStream(recordStream);

    ZeebeTestThreadSupport.setEngineForCurrentThread(containerizedEngine);

    LOGGER.info("Started up Zeebe Testcontainer engine. Now starting deployments and workers...");

    zeebeClient = containerizedEngine.createClient();
    testContext.getApplicationContext().getBean(ZeebeClientProxy.class).swapZeebeClient(zeebeClient);
    testContext.getApplicationContext().getBean(ZeebeAnnotationProcessorRegistry.class).startAll(zeebeClient);

    LOGGER.info("...deployments and workers stated.");
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    if (testContext.getTestException()!=null) {
      LOGGER.warn("Test failure on '"+testContext.getTestMethod()+"'. Tracing workflow engine interals now on INFO for debugging purposes");
      RecordStream recordStream = RecordStream.of(containerizedEngine.getRecordStreamSource());
      recordStream.print(true);

      if (recordStream.incidentRecords().iterator().hasNext()) {
        LOGGER.warn("There were incidents in Zeebe during '"+testContext.getTestMethod()+"', maybe they caused some unexpected behavior for you? Please check below:");
        recordStream.incidentRecords().forEach( record -> {LOGGER.warn(". " + record.getValue());});
      }
    }
    BpmnAssert.resetRecordStream();
    ZeebeTestThreadSupport.cleanupEngineForCurrentThread();

    testContext.getApplicationContext().getBean(ZeebeAnnotationProcessorRegistry.class).stopAll(zeebeClient);
    testContext.getApplicationContext().getBean(ZeebeClientProxy.class).removeZeebeClient();
    zeebeClient.close();
    testContext.getApplicationContext().getBean(ZeebeTestEngineProxy.class).removeZeebeEngine();
    containerizedEngine.reset();
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
