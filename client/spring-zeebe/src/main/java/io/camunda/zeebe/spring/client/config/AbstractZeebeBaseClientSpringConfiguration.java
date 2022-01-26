package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
