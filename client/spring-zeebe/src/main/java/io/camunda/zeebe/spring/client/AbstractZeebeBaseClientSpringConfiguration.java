package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.factory.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.factory.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.postprocessor.ZeebePostProcessorConfiguration;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Abstact class to create ZeebeClient's, the subclass decides about the concrete {@link ZeebeClientObjectFactory}
 * so that it can differ between normal life and test cases
 */
@Import({
  ZeebePostProcessorConfiguration.class,
  ReadAnnotationValueConfiguration.class,
})
public abstract class AbstractZeebeBaseClientSpringConfiguration {



  @Bean
  public ZeebeClientLifecycle zeebeClientLifecycle(
    final ZeebeClientObjectFactory factory,
    final ApplicationEventPublisher publisher) {
    return new ZeebeClientLifecycle(factory, publisher);
  }

  @Bean
  public DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy() {
    return new DefaultCommandExceptionHandlingStrategy(backoffSupplier(), scheduledExecutorService());
  }

  @Bean
  public ScheduledExecutorService scheduledExecutorService() {
    return Executors.newSingleThreadScheduledExecutor();
  }

  @Bean
  public BackoffSupplier backoffSupplier() {
    return new ExponentialBackoffBuilderImpl()
      .maxDelay(1000L)
      .minDelay(50L)
      .backoffFactor(1.5)
      .jitterFactor(0.2)
      .build();
  }
}
