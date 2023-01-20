package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.extension.testcontainer.ContainerizedEngine;
import io.camunda.zeebe.spring.test.proxy.ZeebeTestEngineProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.invoke.MethodHandles;

/**
 * Test execution listener binding the Zeebe engine to current test context.
 */
public class ZeebeTestExecutionListener extends AbstractZeebeTestExecutionListener implements TestExecutionListener, Ordered {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ContainerizedEngine containerizedEngine;

  public void beforeTestClass(@NonNull TestContext testContext) {
    LOGGER.info("Creating Zeebe Testcontainer...");

    final String[] beanNamesForType = testContext.getApplicationContext().getBeanNamesForType(ResolvableType.forClassWithGenerics(ZeebeTestEngineProxy.class, ContainerizedEngine.class));
    containerizedEngine = ((ZeebeTestEngineProxy<ContainerizedEngine>) testContext.getApplicationContext().getBean(beanNamesForType[0])).getCurrentEngine();

    LOGGER.info("...finished creating Zeebe Testcontainer");
  }

  public void beforeTestMethod(@NonNull TestContext testContext) {
    setupWithZeebeEngine(testContext, containerizedEngine, zeebeTestEngine -> {
      LOGGER.info("Create Zeebe Testcontainer engine");
      zeebeTestEngine.start();
      return zeebeTestEngine;
    });
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    cleanup(testContext, containerizedEngine, ContainerizedEngine::reset);
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
