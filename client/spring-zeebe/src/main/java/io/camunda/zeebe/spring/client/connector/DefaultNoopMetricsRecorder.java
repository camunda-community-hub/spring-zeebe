package io.camunda.zeebe.spring.client.connector;

/**
 * Default implementation for MetricsRecorder
 * simply ignoring the counts.
 * Typically you will replace this by a proper Micrometer implementation
 * as you can find in the starter module (activated if Actuator is on the classpath)
 */
public class DefaultNoopMetricsRecorder implements MetricsRecorder {

  @Override
  public void increaseActivated(String metricName, String jobType) {
  }

  @Override
  public void increaseCompleted(String metricName, String jobType) {
  }

  @Override
  public void increaseFailed(String metricName, String jobType) {
  }

  @Override
  public void increaseBpmnError(String metricName, String jobType) {
  }
}
