package io.camunda.zeebe.spring.client.actuator;

import io.camunda.zeebe.spring.client.connector.MetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class MicrometerMetricsRecorder implements MetricsRecorder {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final MeterRegistry meterRegistry;

  private final Map<String, Counter> counterActivated = new HashMap<>();
  private final Map<String, Counter> counterCompleted = new HashMap<>();
  private final Map<String, Counter> counterFailed = new HashMap<>();
  private final Map<String, Counter> counterBpmnError = new HashMap<>();

  public MicrometerMetricsRecorder(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    LOGGER.info("Enabling Micrometer based metrics for spring-zeebe (available via Actuator)");
  }

  protected Counter newCounter(String metricName, String jobType, String action) {
    return meterRegistry.counter(metricName, "action", action, "type", jobType);
  }

  protected void increase(String action, Map<String, Counter> counterMap, String metricName, String jobType) {
    if (!counterMap.containsKey(action)) {
      counterMap.put(action, newCounter(metricName, jobType, action));
    }
    counterMap.get(action).increment();
  }

  @Override
  public void increaseActivated(String metricName, String jobType) {
    increase("activated", counterActivated, metricName, jobType);
  }

  @Override
  public void increaseCompleted(String metricName, String jobType) {
    increase("completed", counterCompleted, metricName, jobType);
  }

  @Override
  public void increaseFailed(String metricName, String jobType) {
    increase("failed", counterFailed, metricName, jobType);
  }

  @Override
  public void increaseBpmnError(String metricName, String jobType) {
    increase("bpmn-error", counterBpmnError, metricName, jobType);
  }

}
