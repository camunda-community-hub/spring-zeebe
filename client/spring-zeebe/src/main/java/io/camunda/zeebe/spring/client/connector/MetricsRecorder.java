package io.camunda.zeebe.spring.client.connector;

public interface MetricsRecorder {

  public void increaseExecuted(String jobType);
  public void increaseFailed(String jobType);
}
