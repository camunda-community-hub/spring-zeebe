package io.camunda.zeebe.spring.client;

import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;
import io.camunda.zeebe.spring.client.connector.ConnectorConfiguration;
import io.camunda.zeebe.spring.client.metrics.DefaultNoopMetricsRecorder;
import io.camunda.zeebe.spring.client.metrics.MetricsDefaultConfiguration;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.camunda.zeebe.spring.client.jobhandling.CommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Abstract class pulling up all configuration that is needed for production as well as for tests.
 *
 * The subclasses add the differences for prod/test
 */
@Import({
  MetricsDefaultConfiguration.class,
  AnnotationProcessorConfiguration.class,
  ConnectorConfiguration.class
})
public abstract class AbstractZeebeBaseClientSpringConfiguration {

  public static class OnMissingCommandExceptionHandlingStrategy implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return context.getBeanFactory().getBeanNamesForType(CommandExceptionHandlingStrategy.class).length<=0;
    }
  }

  @Bean
  @Conditional(value=OnMissingCommandExceptionHandlingStrategy.class)
  public CommandExceptionHandlingStrategy commandExceptionHandlingStrategy() {
    return new DefaultCommandExceptionHandlingStrategy(backoffSupplier(), scheduledExecutorService());
  }

  @Bean
  public JobWorkerManager jobWorkerManager(final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
                                           final SecretProvider secretProvider,
                                           @Autowired(required = false) JsonMapper jsonMapper,
                                           final MetricsRecorder metricsRecorder) {
    return new JobWorkerManager(commandExceptionHandlingStrategy, secretProvider, jsonMapper, metricsRecorder);
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
