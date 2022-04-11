package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.extension.testcontainer.ContainerProperties;
import io.camunda.zeebe.process.test.extension.testcontainer.ContainerizedEngine;
import io.camunda.zeebe.process.test.extension.testcontainer.EngineContainer;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.config.AbstractZeebeBaseClientSpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.lang.invoke.MethodHandles;

@TestConfiguration
public class ZeebeTestClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Do we miss a zeebeClient.close() somewhere?
    @Bean(destroyMethod = "stop")
    @Primary
    public ContainerizedEngine containerBasedZeebeEngine() {
      LOGGER.info("Create Zeebe Container for test run...");
      final EngineContainer container = EngineContainer.getContainer();
      container.start();

      final ContainerizedEngine engine =
        new ContainerizedEngine(
          container.getHost(),
          container.getMappedPort(ContainerProperties.getContainerPort()),
          container.getMappedPort(ContainerProperties.getGatewayPort()));

      LOGGER.info("Started up Zeebe Container for test runs");
      return engine;
    }

    /**
     * Create ZeebeClient not by connecting to a broker, but the in-process EZE ZeebeEngine
     */
    @Bean
    @Primary
    public ZeebeClientObjectFactory testZeebeClientObjectFactory(ZeebeTestEngine zeebeEngine) {
      return new ZeebeClientObjectFactory() {
        @Override
        public ZeebeClient getObject() throws BeansException {
          return zeebeEngine.createClient();
        }
      };
    }

}
