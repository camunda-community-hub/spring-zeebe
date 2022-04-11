package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.engine.EngineFactory;
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
    public ZeebeTestEngine zeebeTestEngine() {
      LOGGER.info("Create Zeebe in-memory engine for test run...");
      ZeebeTestEngine zeebeEngine = EngineFactory.create();
      zeebeEngine.start();
      LOGGER.info("Started up Zeebe in-memory engine for test runs");
      return zeebeEngine;
      // A zeebeEngine is at the same time also a RecordStreamSource (which is required in tests).
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
