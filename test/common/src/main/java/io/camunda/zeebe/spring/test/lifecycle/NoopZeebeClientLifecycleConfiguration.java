package io.camunda.zeebe.spring.test.lifecycle;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.lang.invoke.MethodHandles;

public class NoopZeebeClientLifecycleConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Overwrite lifecycle to disable it in tests
  @Bean
  @Primary
  public NoopZeebeClientLifecycle zeebeClientLifecycle() {
    return new NoopZeebeClientLifecycle();
  }
   */

  /**
   * Create ZeebeClientObjectFactory that does not create a normal ZeebeClient to avoid any real clients
   * being created during startup
   */
  @Bean
  @Primary
  public ZeebeClientObjectFactory testZeebeClientObjectFactory() {
    return new ZeebeClientObjectFactory() {
      @Override
      public ZeebeClient getObject() throws BeansException {
        LOG.info("Not creating any real ZeebeClient in test environment");
        return null;
      }
    };
  }
}
