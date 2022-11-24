package io.camunda.connector.runtime.metrics;

import io.camunda.zeebe.spring.client.connector.MetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MicrometerMetricsRecorder implements MetricsRecorder {

  public static String METRIC_NAME_PREFIX = "camunda.connector.invocations.";

  private final Map<String, Counter> executedCounter = new HashMap<>();
  private final Map<String, Counter> failedCounter = new HashMap<>();
  private final MeterRegistry meterRegistry;

  @Autowired
  public MicrometerMetricsRecorder(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  protected Counter newCounter(String name, String jobType, String action) {
    return meterRegistry.counter(METRIC_NAME_PREFIX + name, "action", action, "type", jobType);
  }

  @Override
  public void increaseExecuted(String name, String jobType) {
    if (!executedCounter.containsKey(jobType)) {
      executedCounter.put(jobType, newCounter(name, jobType, "executed"));
    }
    executedCounter.get(jobType).increment();
  }

  @Override
  public void increaseFailed(String name, String jobType) {
    if (!failedCounter.containsKey(jobType)) {
      failedCounter.put(jobType, newCounter(name, jobType, "failed"));
    }
    failedCounter.get(jobType).increment();
  }

}
