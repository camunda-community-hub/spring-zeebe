package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.engine.EngineFactory;
import io.camunda.zeebe.spring.test.proxy.ZeebeTestEngineProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.TestSocketUtils;

import java.lang.invoke.MethodHandles;

@TestConfiguration
public class EmbeddedZeebeEngineConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Bean
  public ZeebeTestEngineProxy<ZeebeTestEngine> zeebeTestEngineProxy() {
    return initNewEngineProxy();
  }

  public ZeebeTestEngineProxy<ZeebeTestEngine> initNewEngineProxy() {
    int randomPort = TestSocketUtils.findAvailableTcpPort(); // https://github.com/spring-projects/spring-framework/issues/28210
    LOGGER.info("Create Zeebe in-memory engine for test run on random port: " + randomPort + "...");
    final ZeebeTestEngine engine = EngineFactory.create(randomPort);
    EngineUtils.initZeebeEngine(engine);
    engine.start();
    final ZeebeTestEngineProxy<ZeebeTestEngine> zeebeTestEngineZeebeTestEngineProxy = new ZeebeTestEngineProxy<>();
    zeebeTestEngineZeebeTestEngineProxy.swapZeebeEngine(engine);
    return zeebeTestEngineZeebeTestEngineProxy;
  }
}
