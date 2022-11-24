package io.camunda.zeebe.spring.client.connector;

/**
 * Default implementation for MetricsRecorder
 * simply ignoring the counts.
 * Typically you will replace this by a proper Micrometer implementation
 * as you can find in the Conenctor Runtime module
 */
public class DefaultNoopMetricsRecorder implements MetricsRecorder {
  @Override
  public void increaseExecuted(String name, String jobType) {
  }

  @Override
  public void increaseFailed(String name, String jobType) {
  }
}
