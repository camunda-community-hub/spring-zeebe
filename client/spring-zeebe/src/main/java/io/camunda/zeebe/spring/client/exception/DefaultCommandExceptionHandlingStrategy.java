package io.camunda.zeebe.spring.client.exception;

import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.springframework.stereotype.Component;

@Component
public class DefaultCommandExceptionHandlingStrategy {

  public void handleCommandError(JobClient jobClient, ActivatedJob job, FinalCommandStep command, Throwable throwable) {
    // Think about improving error behavior
    // - connection problem -> retry
    // - job non existant any more -> ignore
    throw new RuntimeException("Could not send " + command + " for job " + job + " to Zeebe due to error: " + throwable.getMessage(), throwable);
  }
}
