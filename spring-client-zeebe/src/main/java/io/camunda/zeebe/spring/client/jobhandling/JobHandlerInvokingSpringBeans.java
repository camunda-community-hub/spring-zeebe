package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.command.ThrowErrorCommandStep1.ThrowErrorCommandStep2;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.Loggers;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import io.camunda.zeebe.spring.client.jobhandling.parameter.ParameterResolver;
import io.camunda.zeebe.spring.client.jobhandling.parameter.ParameterResolverStrategy;
import io.camunda.zeebe.spring.client.jobhandling.result.ResultProcessor;
import io.camunda.zeebe.spring.client.jobhandling.result.ResultProcessorStrategy;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/** Zeebe JobHandler that invokes a Spring bean */
public class JobHandlerInvokingSpringBeans implements JobHandler {

  private static final Logger LOG = Loggers.JOB_WORKER_LOGGER;
  private final ZeebeWorkerValue workerValue;
  private final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final MetricsRecorder metricsRecorder;
  private final List<ParameterResolver> parameterResolvers;
  private final ResultProcessor resultProcessor;

  public JobHandlerInvokingSpringBeans(
      ZeebeWorkerValue workerValue,
      CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
      MetricsRecorder metricsRecorder,
      ParameterResolverStrategy parameterResolverStrategy,
      ResultProcessorStrategy resultProcessorStrategy) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.metricsRecorder = metricsRecorder;
    this.parameterResolvers = createParameterResolvers(parameterResolverStrategy);
    this.resultProcessor = createResultProcessor(resultProcessorStrategy);
  }

  private ResultProcessor createResultProcessor(ResultProcessorStrategy resultProcessorStrategy) {
    return resultProcessorStrategy.createProcessor(workerValue.getMethodInfo().getReturnType());
  }

  private List<ParameterResolver> createParameterResolvers(
      ParameterResolverStrategy parameterResolverStrategy) {
    return workerValue.getMethodInfo().getParameters().stream()
        .map(parameterResolverStrategy::createResolver)
        .toList();
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    // TODO: Figuring out parameters and assignments could probably also done only once in the
    // beginning to save some computing time on each invocation
    List<Object> args = createParameters(jobClient, job);
    LOG.trace("Handle {} and invoke worker {}", job, workerValue);
    try {
      metricsRecorder.increase(
          MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_ACTIVATED, job.getType());
      Object result = null;
      try {
        result = workerValue.getMethodInfo().invoke(args.toArray());
        result = resultProcessor.process(result);
      } catch (Throwable t) {
        metricsRecorder.increase(
            MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_FAILED, job.getType());
        // normal exceptions are handled by JobRunnableFactory
        // (https://github.com/camunda-cloud/zeebe/blob/develop/clients/java/src/main/java/io/camunda/zeebe/client/impl/worker/JobRunnableFactory.java#L45)
        // which leads to retrying
        throw t;
      }

      if (workerValue.getAutoComplete()) {
        LOG.trace("Auto completing {}", job);
        // TODO: We should probably move the metrics recording to the callback of a successful
        // command execution to avoid wrong counts
        metricsRecorder.increase(
            MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_COMPLETED, job.getType());
        CommandWrapper command =
            new CommandWrapper(
                createCompleteCommand(jobClient, job, result),
                job,
                commandExceptionHandlingStrategy);
        command.executeAsync();
      }
    } catch (ZeebeBpmnError bpmnError) {
      LOG.trace("Catched BPMN error on {}", job);
      // TODO: We should probably move the metrics recording to the callback of a successful command
      // execution to avoid wrong counts
      metricsRecorder.increase(
          MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_BPMN_ERROR, job.getType());
      CommandWrapper command =
          new CommandWrapper(
              createThrowErrorCommand(jobClient, job, bpmnError),
              job,
              commandExceptionHandlingStrategy);
      command.executeAsync();
    }
  }

  private List<Object> createParameters(JobClient jobClient, ActivatedJob job) {
    return parameterResolvers.stream().map(resolver -> resolver.resolve(jobClient, job)).toList();
  }

  private FinalCommandStep createCompleteCommand(
      JobClient jobClient, ActivatedJob job, Object result) {
    CompleteJobCommandStep1 completeCommand = jobClient.newCompleteCommand(job.getKey());
    if (result != null) {
      if (result.getClass().isAssignableFrom(Map.class)) {
        completeCommand = completeCommand.variables((Map) result);
      } else if (result.getClass().isAssignableFrom(String.class)) {
        completeCommand = completeCommand.variables((String) result);
      } else if (result.getClass().isAssignableFrom(InputStream.class)) {
        completeCommand = completeCommand.variables((InputStream) result);
      } else {
        completeCommand = completeCommand.variables(result);
      }
    }
    return completeCommand;
  }

  private FinalCommandStep<Void> createThrowErrorCommand(
      JobClient jobClient, ActivatedJob job, ZeebeBpmnError bpmnError) {
    ThrowErrorCommandStep2 command =
        jobClient
            .newThrowErrorCommand(job.getKey()) // TODO: PR for taking a job only in command chain
            .errorCode(bpmnError.getErrorCode())
            .errorMessage(bpmnError.getErrorMessage());
    if (bpmnError.getVariables() != null) {
      command.variables(bpmnError.getVariables());
    }
    return command;
  }
}
