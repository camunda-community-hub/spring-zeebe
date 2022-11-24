package io.camunda.zeebe.spring.client.connector;

public interface MetricsRecorder {

  String METRIC_NAME_OUTBOUND_CONNECTOR = "camunda.connector.invocations";
  String METRIC_NAME_JOB = "camunda.job.invocations";

  public void increaseActivated(String metricName, String jobType);
  public void increaseCompleted(String metricName, String jobType);
  public void increaseFailed(String metricName, String jobType);
  public void increaseBpmnError(String metricName, String jobType);
}
