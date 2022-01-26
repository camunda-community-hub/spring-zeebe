package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandWrapper {

  private FinalCommandStep<Void> command;

  private ActivatedJob job;
  private DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;

  private long currentRetryDelay = 50l;

  public CommandWrapper(FinalCommandStep<Void> command, ActivatedJob job, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy) {
    this.command = command;
    this.job = job;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  public void executeAsync() {
    command.send().exceptionally(t -> {
      commandExceptionHandlingStrategy.handleCommandError(this, t);
      return null;
    });
  }

  public void increaseBackoffUsing(BackoffSupplier backoffSupplier) {
    currentRetryDelay = backoffSupplier.supplyRetryDelay(currentRetryDelay);
  }

  public void scheduleExecutionUsing(ScheduledExecutorService scheduledExecutorService) {
    scheduledExecutorService.schedule(this::executeAsync, currentRetryDelay, TimeUnit.MILLISECONDS);
  }

  @Override
  public String toString() {
    return "{" +
      "command=" + command.getClass() +
      ", job=" + job +
      ", currentRetryDelay=" + currentRetryDelay +
      '}';
  }
}
