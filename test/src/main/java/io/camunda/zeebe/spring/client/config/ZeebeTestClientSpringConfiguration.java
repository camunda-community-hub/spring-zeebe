package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.bpmnassert.testengine.EngineFactory;
import io.camunda.zeebe.bpmnassert.testengine.InMemoryEngine;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class ZeebeTestClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

    // Replaces most stuff from https://github.com/camunda-community-hub/eze/blob/76d666759fd11699c0356ca02f697b66d2376e0b/junit-extension/src/main/kotlin/org/camunda/community/eze/EzeExtension.kt

    // Do we miss a zeebeClient.close() somewhere?
    @Bean(destroyMethod = "stop")
    public InMemoryEngine inMemoryZeebeEngine() {
      InMemoryEngine zeebeEngine = EngineFactory.create();
      zeebeEngine.start();
      return zeebeEngine;
      // A zeebeEngine is at the same time also a RecordStreamSource (which is required in tests).
    }

    /**
     * Create ZeebeClient not by connecting to a broker, but the in-process EZE ZeebeEngine
     */
    @Bean
    public ZeebeClientObjectFactory zeebeClientObjectFactory(InMemoryEngine zeebeEngine) {
      return new ZeebeClientObjectFactory() {
        @Override
        public ZeebeClient getObject() throws BeansException {
          return zeebeEngine.createClient();
        }
      };
    }

}
