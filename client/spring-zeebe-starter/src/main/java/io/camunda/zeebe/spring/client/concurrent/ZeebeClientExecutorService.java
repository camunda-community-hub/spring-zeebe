package io.camunda.zeebe.spring.client.concurrent;

import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ZeebeClientExecutorService {

  private ScheduledExecutorService scheduledExecutorService;

  public ZeebeClientExecutorService(ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
  }

  public ScheduledExecutorService get() {
    return scheduledExecutorService;
  }

  public static ZeebeClientExecutorService createDefault(ZeebeClientConfigurationProperties configurationProperties, MeterRegistry meterRegistry) {
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(configurationProperties.getNumJobWorkerExecutionThreads());
    if (meterRegistry != null) {
      MeterBinder threadPoolMetrics = new ExecutorServiceMetrics(
        threadPool, "zeebe_client_thread_pool", Collections.emptyList());
      threadPoolMetrics.bindTo(meterRegistry);
    }
    return new ZeebeClientExecutorService(threadPool);
  }



}
