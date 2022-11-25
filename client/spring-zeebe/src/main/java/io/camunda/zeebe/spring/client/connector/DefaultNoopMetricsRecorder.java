package io.camunda.zeebe.spring.client.connector;

/**
 * Default implementation for MetricsRecorder
 * simply ignoring the counts.
 * Typically you will replace this by a proper Micrometer implementation
 * as you can find in the starter module (activated if Actuator is on the classpath)
 */
public class DefaultNoopMetricsRecorder implements MetricsRecorder {

  @Override
  public void increase(String metricName, String action, String type) {
    // ignore
  }
}
