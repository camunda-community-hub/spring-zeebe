package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import io.camunda.zeebe.spring.test.proxy.ZeebeClientProxy;
import io.camunda.zeebe.spring.test.proxy.ZeebeTestEngineProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;

import java.lang.invoke.MethodHandles;

/**
 * Base class for the two different ZeebeTestExecutionListener classes provided for in-memory vs Testcontainer tests
 */
public class AbstractZeebeTestExecutionListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ZeebeClient zeebeClient;

  /**
   * Registers the ZeebeEngine for test case in relevant places and creates the ZeebeClient
   */
  public void setupWithZeebeEngine(TestContext testContext, ZeebeTestEngine zeebeEngine) {

    testContext.getApplicationContext().getBean(ZeebeTestEngineProxy.class).swapZeebeEngine(zeebeEngine);

    BpmnAssert.initRecordStream(
      RecordStream.of(zeebeEngine.getRecordStreamSource()));

    ZeebeTestThreadSupport.setEngineForCurrentThread(zeebeEngine);

    LOGGER.info("Test engine setup. Now starting deployments and workers...");

    // Not using zeebeEngine.createClient(); to be able to set JsonMapper
    zeebeClient = createClient(testContext, zeebeEngine);

    testContext.getApplicationContext().getBean(ZeebeClientProxy.class).swapZeebeClient(zeebeClient);
    testContext.getApplicationContext().getBean(ZeebeAnnotationProcessorRegistry.class).startAll(zeebeClient);

    LOGGER.info("...deployments and workers stated.");
  }

  public ZeebeClient createClient(TestContext testContext, ZeebeTestEngine zeebeEngine) {

    ZeebeClientBuilder builder = ZeebeClient.newClientBuilder()
      .gatewayAddress(zeebeEngine.getGatewayAddress()).usePlaintext();
    if (testContext.getApplicationContext().getBeanNamesForType(JsonMapper.class).length>0) {
      JsonMapper jsonMapper = testContext.getApplicationContext().getBean(JsonMapper.class);
      builder.withJsonMapper(jsonMapper);
    }
    return builder.build();
  }

  public void cleanup(TestContext testContext, ZeebeTestEngine zeebeEngine) {

    if (testContext.getTestException()!=null) {
      LOGGER.warn("Test failure on '"+testContext.getTestMethod()+"'. Tracing workflow engine internals on INFO for debugging purposes:");
      RecordStream recordStream = RecordStream.of(zeebeEngine.getRecordStreamSource());
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
  }
}
