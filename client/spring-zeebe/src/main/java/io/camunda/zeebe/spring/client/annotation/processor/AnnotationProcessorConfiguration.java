package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadZeebeDeploymentValue;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadZeebeWorkerValue;
import java.util.List;

import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ReadAnnotationValueConfiguration.class)
public class AnnotationProcessorConfiguration {

  @Bean
  public ZeebeAnnotationProcessorRegistry zeebeAnnotationProcessorRegistry(final List<AbstractZeebeAnnotationProcessor> processors) {
    return new ZeebeAnnotationProcessorRegistry(processors);
  }

  @Bean
  public ZeebeDeploymentAnnotationProcessor deploymentPostProcessor(final ReadZeebeDeploymentValue reader) {
    return new ZeebeDeploymentAnnotationProcessor(reader);
  }

  @Bean
  public ZeebeWorkerAnnotationProcessor zeebeWorkerPostProcessor(final ReadZeebeWorkerValue reader,
                                                                 final JobWorkerManager jobWorkerManager,
                                                                 final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers) {
    return new ZeebeWorkerAnnotationProcessor(reader, jobWorkerManager, zeebeWorkerValueCustomizers);
  }

}
