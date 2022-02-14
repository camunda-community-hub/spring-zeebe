package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
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

  private InMemoryEngine zeebeEngine;

  public void beforeTestClass(@NonNull TestContext testContext) {
    zeebeEngine = testContext.getApplicationContext().getBean(InMemoryEngine.class);
  }

  public void beforeTestMethod(@NonNull TestContext testContext) {
    RecordStreamSourceStore.init(zeebeEngine.getRecordStream());
    ZeebeTestThreadSupport.setEngineForCurrentThread(zeebeEngine);
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    if (testContext.getTestException()!=null) {
      LOGGER.warn("Test failure on '"+testContext.getTestMethod()+"'. Tracing workflow engine interals now on INFO for debugging purposes");
      zeebeEngine.getRecordStream().print(true);

      if (zeebeEngine.getRecordStream().incidentRecords().iterator().hasNext()) {
        LOGGER.warn("There were incidents in Zeebe during '"+testContext.getTestMethod()+"', maybe they caused some unexpected behavior for you? Please check below:");
        zeebeEngine.getRecordStream().incidentRecords().forEach( record -> {LOGGER.warn(". " + record.getValue());});
      }
    }
    RecordStreamSourceStore.reset();
    ZeebeTestThreadSupport.cleanupEngineForCurrentThread();
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
