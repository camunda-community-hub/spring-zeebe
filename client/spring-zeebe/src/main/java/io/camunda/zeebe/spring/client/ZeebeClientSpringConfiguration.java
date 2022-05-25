package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

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
  public ZeebeClientLifecycle zeebeClientLifecycle(final ZeebeClientObjectFactory factory, final ZeebeAnnotationProcessorRegistry proxy, final ApplicationEventPublisher publisher) {
    return new ZeebeClientLifecycle(factory, proxy, publisher);
  }

  @Bean
  public ZeebeClientObjectFactory zeebeClientObjectFactory(ZeebeClientBuilder zeebeClientBuilder) {
    return new ZeebeClientObjectFactory() {
      @Override
      public ZeebeClient getObject() throws BeansException {
        LOG.info("Creating ZeebeClient using ZeebeClientBuilder [" + zeebeClientBuilder + "]");
        return zeebeClientBuilder.build();
      }
    };
  }

}
