package io.camunda.zeebe.spring.client.configuration;

import static io.camunda.zeebe.spring.client.configuration.PropertyUtil.*;
import static io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties.*;

import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnMissingBean(ZeebeClientExecutorService.class)
public class ExecutorServiceConfiguration {

  private final ZeebeClientConfigurationProperties configurationProperties;
  private final CamundaClientProperties camundaClientProperties;

  public ExecutorServiceConfiguration(
      ZeebeClientConfigurationProperties configurationProperties,
      CamundaClientProperties camundaClientProperties) {
    this.configurationProperties = configurationProperties;
    this.camundaClientProperties = camundaClientProperties;
  }

  @Bean
  public ZeebeClientExecutorService zeebeClientThreadPool(
      @Autowired(required = false) MeterRegistry meterRegistry) {
    ScheduledExecutorService threadPool =
        Executors.newScheduledThreadPool(
            getOrLegacyOrDefault(
                "NumJobWorkerExecutionThreads",
                () -> camundaClientProperties.getZeebe().getExecutionThreads(),
                configurationProperties::getNumJobWorkerExecutionThreads,
                DEFAULT.getNumJobWorkerExecutionThreads(),
                null));
    if (meterRegistry != null) {
      MeterBinder threadPoolMetrics =
          new ExecutorServiceMetrics(
              threadPool, "zeebe_client_thread_pool", Collections.emptyList());
      threadPoolMetrics.bindTo(meterRegistry);
    }
    configurationProperties.setOwnsJobWorkerExecutor(true);
    Optional.of(camundaClientProperties)
        .map(CamundaClientProperties::getZeebe)
        .ifPresent(p -> p.setOwnsJobWorkerExecutor(true));
    return new ZeebeClientExecutorService(threadPool);
  }
}
