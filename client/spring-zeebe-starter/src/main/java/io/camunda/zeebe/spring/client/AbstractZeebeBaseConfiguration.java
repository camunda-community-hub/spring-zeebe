package io.camunda.zeebe.spring.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;
import io.camunda.zeebe.spring.client.configuration.ConnectorConfiguration;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.configuration.MetricsDefaultConfiguration;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.camunda.zeebe.spring.client.jobhandling.CommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Abstract class pulling up all configuration that is needed for production as well as for tests.
 *
 * The subclasses add the differences for prod/test
 */
@Import({
  AnnotationProcessorConfiguration.class,
  ConnectorConfiguration.class,
  MetricsDefaultConfiguration.class
})
public abstract class AbstractZeebeBaseConfiguration {

  public static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

  @Bean
  @ConditionalOnMissingBean
  public ZeebeClientExecutorService zeebeClientExecutorService() {
    return ZeebeClientExecutorService.createDefault();
  }

  @Bean
  @ConditionalOnMissingBean
  public CommandExceptionHandlingStrategy commandExceptionHandlingStrategy(ZeebeClientExecutorService scheduledExecutorService) {
    return new DefaultCommandExceptionHandlingStrategy(backoffSupplier(), scheduledExecutorService.get());
  }

  @Bean
  public JobWorkerManager jobWorkerManager(final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
                                           final SecretProvider secretProvider,
                                           final OutboundConnectorFactory connectorFactory,
                                           final JsonMapper jsonMapper,
                                           final MetricsRecorder metricsRecorder) {
    return new JobWorkerManager(commandExceptionHandlingStrategy, secretProvider, connectorFactory, jsonMapper, metricsRecorder);
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
