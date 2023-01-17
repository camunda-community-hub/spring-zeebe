package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.extension.testcontainer.ContainerizedEngine;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import io.camunda.zeebe.spring.test.proxy.ZeebeTestEngineProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

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
    LOGGER.info("Create Zeebe Testcontainer engine");
    if (Optional.ofNullable(testContext.getAttribute("NOT_STARTED")).map(o -> (Boolean) o).orElse(false)) {
      containerizedEngine.start();
      setupWithZeebeEngine(testContext, containerizedEngine);
    }
    // TODO: Play nicely with this. It probably should be somewhere in the parent class
    testContext.getApplicationContext().getBean(ZeebeAnnotationProcessorRegistry.class).startAll(testContext.getApplicationContext().getBean(ZeebeClient.class));
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    cleanup(testContext, containerizedEngine);
    containerizedEngine.reset();
    testContext.setAttribute("NOT_STARTED", Boolean.TRUE);
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
