package io.camunda.zeebe.spring.client.configuration;

import io.camunda.zeebe.client.ClientProperties;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.model.bpmn.impl.instance.zeebe.ZeebePropertiesImpl;
import io.camunda.zeebe.model.bpmn.instance.zeebe.ZeebeProperties;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;
import io.camunda.zeebe.spring.client.jobhandling.CommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.camunda.zeebe.spring.client.properties.PropertyBasedZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@ConditionalOnProperty(prefix = "zeebe.client", name = "enabled", havingValue = "true",  matchIfMissing = true)
@Import(AnnotationProcessorConfiguration.class)
@EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
public class ZeebeClientAllAutoConfiguration {

  private final ZeebeClientConfigurationProperties configurationProperties;
  public ZeebeClientAllAutoConfiguration(ZeebeClientConfigurationProperties configurationProperties) {
    // TODO Remove workaround as soon as https://github.com/camunda/zeebe/issues/14176 is fixed
    if(configurationProperties.getWorker().getDefaultName() == null) {
      configurationProperties.getWorker().setDefaultName(ClientProperties.DEFAULT_JOB_WORKER_NAME);
    }
    this.configurationProperties = configurationProperties;
  }

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
                                           final JsonMapper jsonMapper,
                                           final MetricsRecorder metricsRecorder) {
    return new JobWorkerManager(commandExceptionHandlingStrategy, jsonMapper, metricsRecorder);
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

  @Bean("propertyBasedZeebeWorkerValueCustomizer")
  @ConditionalOnMissingBean(name = "propertyBasedZeebeWorkerValueCustomizer")
  public ZeebeWorkerValueCustomizer propertyBasedZeebeWorkerValueCustomizer() {
    return new PropertyBasedZeebeWorkerValueCustomizer(this.configurationProperties);
  }


}
