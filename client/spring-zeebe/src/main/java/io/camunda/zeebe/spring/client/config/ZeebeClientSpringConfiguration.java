package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;

import org.springframework.context.annotation.Bean;

public class ZeebeClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public ZeebeClientObjectFactory zeebeClientObjectFactory(ZeebeClientBuilder zeebeClientBuilder) {
    return () -> zeebeClientBuilder.build();
  }

}
