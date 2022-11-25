package io.camunda.zeebe.spring.client.connector;

public interface MetricsRecorder {

  String METRIC_NAME_OUTBOUND_CONNECTOR = "camunda.connector.invocations";
  String METRIC_NAME_JOB = "camunda.job.invocations";

  String ACTION_ACTIVATED = "activated";
  String ACTION_COMPLETED = "completed";
  String ACTION_FAILED = "failed";
  String ACTION_BPMN_ERROR = "bpmn-error";

  public void increase(String metricName, String action, String type);
}
