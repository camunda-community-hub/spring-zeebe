package io.camunda.zeebe.spring.client.connector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Super basic default implementation for collecting metrics.
 * Typically you will replace this by a proper Micrometer implementation
 * as you can find in the Conenctor Runtime module
 */
public class DefaultMetricsRecorder implements MetricsRecorder {
  private Map<String, AtomicLong> executed = new HashMap<>();
  private Map<String, AtomicLong> failed = new HashMap<>();

  @Override
  public void increaseExecuted(String jobType) {
    if (!executed.containsKey(jobType)) {
      executed.put(jobType, new AtomicLong(0));
    }
    executed.get(jobType).incrementAndGet();
  }

  @Override
  public void increaseFailed(String jobType) {
    if (!failed.containsKey(jobType)) {
      failed.put(jobType, new AtomicLong(0));
    }
    failed.get(jobType).incrementAndGet();
  }
}
