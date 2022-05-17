package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.spring.client.factory.ZeebeClientObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;

/**
 * Pulled in by @EnableZeebeClient as configuration.
 *
 * ZeebeClientBuilder is provided by ZeebeClientStarterAutoConfiguration (which is enabled by META-INF of Spring Boot Starter)
 *
 * ZeebeClientObjectFactory can create ZeebeClients, it does that by being used as ZeebeClientLifecycle in AbstractZeebeBaseClientSpringConfiguration
 */
public class ZeebeClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Bean
  public ZeebeClientObjectFactory zeebeClientObjectFactory(ZeebeClientBuilder zeebeClientBuilder) {
    return new ZeebeClientObjectFactory() {
      @Override
      public ZeebeClient getObject() throws BeansException {
        LOG.info("Creating ZeebeClient using normal ZeebeClientBuilder [" + zeebeClientBuilder + "]");
        new Exception().printStackTrace();
        return zeebeClientBuilder.build();
      }
    };
  }

}
