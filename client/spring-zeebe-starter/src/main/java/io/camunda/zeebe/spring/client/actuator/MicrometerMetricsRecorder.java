package io.camunda.zeebe.spring.client.actuator;

import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class MicrometerMetricsRecorder implements MetricsRecorder {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final MeterRegistry meterRegistry;
  private final Map<String, Counter> counters = new HashMap<>();

  public MicrometerMetricsRecorder(final MeterRegistry meterRegistry) {
    LOGGER.info("Enabling Micrometer based metrics for spring-zeebe (available via Actuator)");
    this.meterRegistry = meterRegistry;
  }

  protected Counter newCounter(String metricName, String action, String jobType) {
    return meterRegistry.counter(metricName, "action", action, "type", jobType);
  }

  @Override
  public void increase(String metricName, String action, String jobType) {
    String key = metricName + "#" + action;
    if (!counters.containsKey(key)) {
      counters.put(key, newCounter(metricName, action, jobType));
    }
    counters.get(key).increment();
  }

  @Override
  public void executeWithTimer(String metricName, Runnable methodToExecute) {
    Timer timer = meterRegistry.timer(metricName);
    timer.record(methodToExecute);
  }

}
