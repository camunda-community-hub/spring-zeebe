package io.camunda.zeebe.spring.client.config.processor;

import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeDeploymentValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import java.util.List;

import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ReadAnnotationValueConfiguration.class)
public class PostProcessorConfiguration {

  @Bean
  public SubscriptionBuilderPostProcessor subscriptionBuilderPostProcessor(
    final List<BeanInfoPostProcessor> processors, final ZeebeClientLifecycle clientLifecycle) {
    return new SubscriptionBuilderPostProcessor(processors, clientLifecycle);
  }

  @Bean
  public DeploymentPostProcessor deploymentPostProcessor(final ReadZeebeDeploymentValue reader) {
    return new DeploymentPostProcessor(reader);
  }

  @Bean
  public ZeebeWorkerPostProcessor zeebeWorkerPostProcessor(final ReadZeebeWorkerValue reader, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy, BackoffSupplier backoffSupplier) {
    return new ZeebeWorkerPostProcessor(reader, commandExceptionHandlingStrategy, backoffSupplier);
  }

}
