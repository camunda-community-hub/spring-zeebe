package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;
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

  private ZeebeTestEngine zeebeEngine;

  public void beforeTestClass(@NonNull TestContext testContext) {
    zeebeEngine = testContext.getApplicationContext().getBean(ZeebeTestEngine.class);
  }

  public void beforeTestMethod(@NonNull TestContext testContext) {
    final RecordStream recordStream = RecordStream.of(zeebeEngine.getRecordStreamSource());
    BpmnAssert.initRecordStream(recordStream);

    ZeebeTestThreadSupport.setEngineForCurrentThread(zeebeEngine);
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
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
