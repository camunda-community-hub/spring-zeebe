package io.camunda.zeebe.spring.client;

import io.camunda.connector.api.secret.SecretStore;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.client.jobhandling.SpringSecretProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Abstract class pulling up all configuration that is needed for production as well as for tests.
 *
 * The subclasses add the differences for prod/test
 */
@Import({
  AnnotationProcessorConfiguration.class
})
public abstract class AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy() {
    return new DefaultCommandExceptionHandlingStrategy(backoffSupplier(), scheduledExecutorService());
  }

  @Bean
  public SecretStore secretStore(Environment env) {
    return new SecretStore(
      new SpringSecretProvider(env));
  }

  @Bean
  public JobWorkerManager jobWorkerManager(final DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
                                           SecretStore secretStore,
                                           @Autowired(required = false) JsonMapper jsonMapper) {
    return new JobWorkerManager(commandExceptionHandlingStrategy, secretStore, jsonMapper);
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
