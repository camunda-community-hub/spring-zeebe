package io.camunda.zeebe.spring.client.configuration;

import static io.camunda.zeebe.spring.client.configuration.PropertyUtil.getOrLegacyOrDefault;
import static io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties.DEFAULT;

import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.processor.AnnotationProcessorConfiguration;
import io.camunda.zeebe.spring.client.configuration.condition.ZeebeClientCondition;
import io.camunda.zeebe.spring.client.jobhandling.CommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.jobhandling.parameter.DefaultParameterResolverStrategy;
import io.camunda.zeebe.spring.client.jobhandling.parameter.ParameterResolverStrategy;
import io.camunda.zeebe.spring.client.jobhandling.result.DefaultResultProcessorStrategy;
import io.camunda.zeebe.spring.client.jobhandling.result.ResultProcessorStrategy;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.PropertyBasedZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

@Conditional(ZeebeClientCondition.class)
@Import({AnnotationProcessorConfiguration.class, JsonMapperConfiguration.class})
@EnableConfigurationProperties({
  ZeebeClientConfigurationProperties.class,
  CamundaClientProperties.class
})
public class ZeebeClientAllAutoConfiguration {

  private final ZeebeClientConfigurationProperties configurationProperties;
  private final CamundaClientProperties camundaClientProperties;

  public ZeebeClientAllAutoConfiguration(
      ZeebeClientConfigurationProperties configurationProperties,
      CamundaClientProperties camundaClientProperties) {
    this.configurationProperties = configurationProperties;
    this.camundaClientProperties = camundaClientProperties;
  }

  @Bean
  @ConditionalOnMissingBean
  public ZeebeClientExecutorService zeebeClientExecutorService() {
    return ZeebeClientExecutorService.createDefault(
        getOrLegacyOrDefault(
            "NumJobWorkerExecutionThreads",
            () -> camundaClientProperties.getZeebe().getExecutionThreads(),
            configurationProperties::getNumJobWorkerExecutionThreads,
            DEFAULT.getNumJobWorkerExecutionThreads(),
            null));
  }

  @Bean
  @ConditionalOnMissingBean
  public CommandExceptionHandlingStrategy commandExceptionHandlingStrategy(
      ZeebeClientExecutorService scheduledExecutorService) {
    return new DefaultCommandExceptionHandlingStrategy(
        backoffSupplier(), scheduledExecutorService.get());
  }

  @Bean
  @ConditionalOnMissingBean
  public ParameterResolverStrategy parameterResolverStrategy(JsonMapper jsonMapper) {
    return new DefaultParameterResolverStrategy(jsonMapper);
  }

  @Bean
  @ConditionalOnMissingBean
  public ResultProcessorStrategy resultProcessorStrategy() {
    return new DefaultResultProcessorStrategy();
  }

  @Bean
  public JobWorkerManager jobWorkerManager(
      final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
      final MetricsRecorder metricsRecorder,
      final ParameterResolverStrategy parameterResolverStrategy,
      final ResultProcessorStrategy resultProcessorStrategy) {
    return new JobWorkerManager(
        commandExceptionHandlingStrategy,
        metricsRecorder,
        parameterResolverStrategy,
        resultProcessorStrategy);
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
    return new PropertyBasedZeebeWorkerValueCustomizer(
        this.configurationProperties, camundaClientProperties);
  }
}
