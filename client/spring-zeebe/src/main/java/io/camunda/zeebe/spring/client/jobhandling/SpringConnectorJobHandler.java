package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.connector.api.error.BpmnError;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.runtime.util.outbound.ConnectorJobHandler;
import io.camunda.connector.runtime.util.outbound.ConnectorResult;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

public class SpringConnectorJobHandler extends ConnectorJobHandler {

  private SecretProvider secretProvider;
  private final DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;

  public SpringConnectorJobHandler(OutboundConnectorFunction call, SecretProvider secretProvider,
    DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy) {
    super(call);
    this.secretProvider = secretProvider;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  @Override
  protected SecretProvider getSecretProvider() {
    if (secretProvider == null) {
      secretProvider = super.getSecretProvider();
    }
    return secretProvider;
  }

  @Override
  protected void failJob(JobClient client, ActivatedJob job, Exception exception) {
    // rethrow exception, will be handled by JobRunnableFactory
    throw new RuntimeException(exception);
  }

  @Override
  protected void throwBpmnError(JobClient client, ActivatedJob job, BpmnError value) {
    new CommandWrapper(
      client.newThrowErrorCommand(job.getKey()).errorCode(value.getCode()).errorMessage(value.getMessage()),
      job,
      commandExceptionHandlingStrategy
    ).executeAsync();
  }

  @Override protected void completeJob(JobClient client, ActivatedJob job, ConnectorResult result) {
    new CommandWrapper(
      JobHandlerInvokingSpringBeans.createCompleteCommand(client, job, result),
      job,
      commandExceptionHandlingStrategy).executeAsync();
  }
}
