package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Test execution listener binding the Zeebe engine to current test context.
 */
public class ZeebeTestExecutionListener implements TestExecutionListener, Ordered {

  private InMemoryEngine zeebeEngine;

  public void beforeTestClass(@NonNull TestContext testContext) {
    zeebeEngine = testContext.getApplicationContext().getBean(InMemoryEngine.class);
  }

  public void beforeTestMethod(@NonNull TestContext testContext) {
    RecordStreamSourceStore.init(zeebeEngine.getRecordStream());
    ZeebeTestThreadSupport.setEngineForCurrentThread(zeebeEngine);
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    RecordStreamSourceStore.reset();
    ZeebeTestThreadSupport.cleanupEngineForCurrentThread();
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
