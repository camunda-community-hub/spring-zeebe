package io.camunda.zeebe.spring.client.connector;

public interface MetricsRecorder {

  public void increaseExecuted(String name, String jobType);
  public void increaseFailed(String name, String jobType);
}
