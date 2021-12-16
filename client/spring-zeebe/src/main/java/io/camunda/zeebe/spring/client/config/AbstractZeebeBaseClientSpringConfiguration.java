package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import io.camunda.zeebe.spring.client.exception.DefaultCommandExceptionHandlingStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Properties;

@Import({
  PostProcessorConfiguration.class,
  ReadAnnotationValueConfiguration.class,
})
public abstract class AbstractZeebeBaseClientSpringConfiguration {

  public static final ZeebeClientBuilderImpl DEFAULT =
    (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());

  @Bean
  public ZeebeClientLifecycle zeebeClientLifecycle(
    final ZeebeClientObjectFactory factory,
    final ApplicationEventPublisher publisher) {
    return new ZeebeClientLifecycle(factory, publisher);
  }

  @Bean
  public DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy() {
    return new DefaultCommandExceptionHandlingStrategy();
  }
}
