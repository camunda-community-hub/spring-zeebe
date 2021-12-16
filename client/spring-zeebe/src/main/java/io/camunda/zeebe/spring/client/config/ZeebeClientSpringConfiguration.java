package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.exception.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

public class ZeebeClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public ZeebeClientObjectFactory zeebeClientObjectFactory(ZeebeClientBuilder zeebeClientBuilder) {
    return () -> zeebeClientBuilder.build();
  }

}
