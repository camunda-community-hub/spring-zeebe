package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.connector.api.error.BpmnError;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.runtime.util.outbound.ConnectorJobHandler;
import io.camunda.connector.runtime.util.outbound.ConnectorResult;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.connector.MetricsRecorder;

public class SpringConnectorJobHandler extends ConnectorJobHandler {

  private final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final MetricsRecorder metrics;

  public SpringConnectorJobHandler(OutboundConnectorFunction call, SecretProvider secretProvider,
                                   CommandExceptionHandlingStrategy commandExceptionHandlingStrategy, MetricsRecorder metrics) {
    super(call, secretProvider);
    this.metrics = metrics;
    this.secretProvider = secretProvider;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  @Override
  protected void failJob(JobClient client, ActivatedJob job, Exception exception) {
    metrics.increaseFailed(job.getType());
    // rethrow exception, will be handled by JobRunnableFactory
    throw new RuntimeException(exception);
  }

  @Override
  protected void throwBpmnError(JobClient client, ActivatedJob job, BpmnError value) {
    metrics.increaseExecuted(job.getType());
    new CommandWrapper(
      client.newThrowErrorCommand(job.getKey()).errorCode(value.getCode()).errorMessage(value.getMessage()),
      job,
      commandExceptionHandlingStrategy
    ).executeAsync();
  }

  @Override protected void completeJob(JobClient client, ActivatedJob job, ConnectorResult result) {
    metrics.increaseExecuted(job.getType());
    new CommandWrapper(
      JobHandlerInvokingSpringBeans.createCompleteCommand(client, job, result.getVariables()),
      job,
      commandExceptionHandlingStrategy).executeAsync();
  }
}
