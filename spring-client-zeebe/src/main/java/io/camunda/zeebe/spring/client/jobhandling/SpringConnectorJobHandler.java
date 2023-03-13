package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.connector.api.error.BpmnError;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.ConnectorJobHandler;
import io.camunda.connector.runtime.util.outbound.ConnectorResult;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;

public class SpringConnectorJobHandler extends ConnectorJobHandler {

  private final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final OutboundConnectorConfiguration connectorConfiguration;
  private final MetricsRecorder metrics;

  public SpringConnectorJobHandler(
    OutboundConnectorConfiguration connectorConfiguration,
    OutboundConnectorFunction connectorFunction,
    SecretProvider secretProvider,
    CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
    MetricsRecorder metrics) {

    super(connectorFunction, secretProvider);
    this.connectorConfiguration = connectorConfiguration;
    this.metrics = metrics;
    this.secretProvider = secretProvider;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  @Override
  public void handle(JobClient client, ActivatedJob job) {
    metrics.increase(MetricsRecorder.METRIC_NAME_OUTBOUND_CONNECTOR, MetricsRecorder.ACTION_ACTIVATED, connectorConfiguration.getType());
    super.handle(client, job);
  }

  @Override
  protected void failJob(JobClient client, ActivatedJob job, Exception exception) {
    metrics.increase(MetricsRecorder.METRIC_NAME_OUTBOUND_CONNECTOR, MetricsRecorder.ACTION_FAILED, connectorConfiguration.getType());
    // rethrow exception, will be handled by JobRunnableFactory
    throw new RuntimeException(exception);
  }

  @Override
  protected void throwBpmnError(JobClient client, ActivatedJob job, BpmnError value) {
    metrics.increase(MetricsRecorder.METRIC_NAME_OUTBOUND_CONNECTOR, MetricsRecorder.ACTION_BPMN_ERROR, connectorConfiguration.getType());
    new CommandWrapper(
      client.newThrowErrorCommand(job.getKey()).errorCode(value.getCode()).errorMessage(value.getMessage()),
      job,
      commandExceptionHandlingStrategy
    ).executeAsync();
  }

  @Override
  protected void completeJob(JobClient client, ActivatedJob job, ConnectorResult result) {
    metrics.increase(MetricsRecorder.METRIC_NAME_OUTBOUND_CONNECTOR, MetricsRecorder.ACTION_COMPLETED, connectorConfiguration.getType());
    new CommandWrapper(
      JobHandlerInvokingSpringBeans.createCompleteCommand(client, job, result.getVariables()),
      job,
      commandExceptionHandlingStrategy).executeAsync();
  }
}
