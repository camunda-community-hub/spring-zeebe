package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.extension.testcontainer.ContainerProperties;
import io.camunda.zeebe.process.test.extension.testcontainer.ContainerizedEngine;
import io.camunda.zeebe.process.test.extension.testcontainer.EngineContainer;
import io.camunda.zeebe.spring.test.proxy.ZeebeTestEngineProxy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestContainerZeebeEngineConfiguration {

  @Bean
  public ZeebeTestEngineProxy<ContainerizedEngine> zeebeTestEngineProxy() {
    final ZeebeTestEngineProxy<ContainerizedEngine> zeebeTestEngineProxy = new ZeebeTestEngineProxy<>();
    final EngineContainer container = EngineContainer.getContainer();
    container.start();
    final ContainerizedEngine containerizedEngine = new ContainerizedEngine(
      container.getHost(),
      container.getMappedPort(ContainerProperties.getContainerPort()),
      container.getMappedPort(ContainerProperties.getGatewayPort()));
    containerizedEngine.start();
    EngineUtils.initZeebeEngine(containerizedEngine);
    zeebeTestEngineProxy.swapZeebeEngine(containerizedEngine);
    return zeebeTestEngineProxy;
  }
}
