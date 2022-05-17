package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.spring.client.factory.ZeebeClientObjectFactory;
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
  public ZeebeClientObjectFactory zeebeClientObjectFactory(ZeebeClientBuilder zeebeClientBuilder) {
    return () -> zeebeClientBuilder.build();
  }

}
