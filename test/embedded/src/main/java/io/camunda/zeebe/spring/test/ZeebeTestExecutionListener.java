package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.engine.EngineFactory;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.util.TestSocketUtils;

/** Test execution listener binding the Zeebe engine to current test context. */
public class ZeebeTestExecutionListener extends AbstractZeebeTestExecutionListener
    implements TestExecutionListener, Ordered {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ZeebeTestEngine zeebeEngine;

  public void beforeTestMethod(@NonNull TestContext testContext) {
    int randomPort =
        TestSocketUtils
            .findAvailableTcpPort(); // can be replaced with TestSocketUtils once available:
    // https://github.com/spring-projects/spring-framework/pull/29132

    LOGGER.info("Create Zeebe in-memory engine for test run on random port: " + randomPort + "...");
    zeebeEngine = EngineFactory.create(randomPort);
    zeebeEngine.start();

    setupWithZeebeEngine(testContext, zeebeEngine);
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    cleanup(testContext, zeebeEngine);
    zeebeEngine.stop();
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
