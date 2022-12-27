package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

/**
 * Pulled in by @EnableZeebeClient as configuration.
 *
 * ZeebeClientBuilder is provided by ZeebeClientStarterAutoConfiguration (which is enabled by META-INF of Spring Boot Starter)
 *
 * ZeebeClientObjectFactory can create ZeebeClients, it does that by being used as ZeebeClientLifecycle in AbstractZeebeBaseClientSpringConfiguration
 */
public class ZeebeClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public ZeebeClientLifecycle zeebeClientLifecycle(final ZeebeClientObjectFactory factory, final ZeebeAnnotationProcessorRegistry proxy, final ApplicationEventPublisher publisher) {
    return new ZeebeClientLifecycle(factory, proxy, publisher);
  }
}
