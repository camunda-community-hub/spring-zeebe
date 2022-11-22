package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.connector.OutboundConnectorManager;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

public class AnnotationProcessorConfiguration {

  @Bean
  public ZeebeAnnotationProcessorRegistry zeebeAnnotationProcessorRegistry(final List<AbstractZeebeAnnotationProcessor> processors) {
    return new ZeebeAnnotationProcessorRegistry(processors);
  }

  @Bean
  public ZeebeDeploymentAnnotationProcessor deploymentPostProcessor() {
    return new ZeebeDeploymentAnnotationProcessor();
  }

  @Bean
  public OutboundConnectorAnnotationProcessor outboundConnectorAnnotationProcessor(final OutboundConnectorManager outboundConnectorManager) {
    return new OutboundConnectorAnnotationProcessor(outboundConnectorManager);
  }

  @Bean
  public ZeebeWorkerAnnotationProcessor zeebeWorkerPostProcessor(final JobWorkerManager jobWorkerManager,
                                                                 final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers,
                                                                 final Environment environment) { // can#t use @Value because it is only evaluated after constructors are executed
    String defaultWorkerType = environment.getProperty("zeebe.client.worker.default-type", (String)null);
    String defaultJobWorkerName = environment.getProperty("zeebe.client.worker.default-name", (String)null);
    return new ZeebeWorkerAnnotationProcessor(jobWorkerManager, zeebeWorkerValueCustomizers, defaultWorkerType, defaultJobWorkerName);
  }

}
