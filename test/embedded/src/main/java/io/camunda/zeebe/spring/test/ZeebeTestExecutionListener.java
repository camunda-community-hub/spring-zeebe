package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.engine.EngineFactory;
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
import org.springframework.util.SocketUtils;

import java.lang.invoke.MethodHandles;

/**
 * Test execution listener binding the Zeebe engine to current test context.
 */
public class ZeebeTestExecutionListener implements TestExecutionListener, Ordered {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ZeebeTestEngine zeebeEngine;
  private ZeebeClient zeebeClient;

  public void beforeTestMethod(@NonNull TestContext testContext) {
    int randomPort = SocketUtils.findAvailableTcpPort(); // can be replaced with TestSocketUtils once available: https://github.com/spring-projects/spring-framework/issues/28210

    LOGGER.info("Create Zeebe in-memory engine for test run on random port: " + randomPort + "...");
    zeebeEngine = EngineFactory.create(randomPort);
    zeebeEngine.start();
    testContext.getApplicationContext().getBean(ZeebeTestEngineProxy.class).swapZeebeEngine(zeebeEngine);

    final RecordStream recordStream = RecordStream.of(zeebeEngine.getRecordStreamSource());
    BpmnAssert.initRecordStream(recordStream);

    ZeebeTestThreadSupport.setEngineForCurrentThread(zeebeEngine);

    LOGGER.info("Started up Zeebe in-memory engine for test runs. Now starting deployments and workers...");

    zeebeClient = zeebeEngine.createClient();
    testContext.getApplicationContext().getBean(ZeebeClientProxy.class).swapZeebeClient(zeebeClient);
    testContext.getApplicationContext().getBean(ZeebeAnnotationProcessorRegistry.class).startAll(zeebeClient);

    LOGGER.info("...deployments and workers stated.");
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    if (testContext.getTestException()!=null) {
      LOGGER.warn("Test failure on '"+testContext.getTestMethod()+"'. Tracing workflow engine interals now on INFO for debugging purposes");
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
    zeebeEngine.stop();
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
