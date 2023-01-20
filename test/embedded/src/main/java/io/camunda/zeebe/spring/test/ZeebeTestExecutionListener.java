package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
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

  private ZeebeTestEngine zeebeEngine;

  @Override
  public void beforeTestClass(TestContext testContext) {
    final String[] beanNamesForType = testContext.getApplicationContext().getBeanNamesForType(ResolvableType.forClassWithGenerics(ZeebeTestEngineProxy.class, ZeebeTestEngine.class));
    zeebeEngine = ((ZeebeTestEngineProxy<ZeebeTestEngine>) testContext.getApplicationContext().getBean(beanNamesForType[0])).getCurrentEngine();
  }

  public void beforeTestMethod(@NonNull TestContext testContext) {
    setupWithZeebeEngine(testContext, zeebeEngine, zeebeTestEngine -> {
      zeebeEngine = testContext.getApplicationContext().getBean(EmbeddedZeebeEngineConfiguration.class).initNewEngineProxy().getCurrentEngine();
      return zeebeEngine;
    });
  }

  public void afterTestMethod(@NonNull TestContext testContext) {
    cleanup(testContext, zeebeEngine, ZeebeTestEngine::stop);
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
