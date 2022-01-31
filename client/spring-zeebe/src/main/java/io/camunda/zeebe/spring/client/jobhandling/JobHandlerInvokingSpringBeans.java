package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.Loggers;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariablesAsType;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Zeebe JobHandler that invokes a Spring bean
 */
public class JobHandlerInvokingSpringBeans implements JobHandler {

  private static final Logger LOG = Loggers.JOB_WORKER_LOGGER;
  private ZeebeWorkerValue workerValue;
  private DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;

  public JobHandlerInvokingSpringBeans(ZeebeWorkerValue workerValue, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    // TODO: Figuring out parameters and assignments could probably also done only once in the beginning to save some computing time on each invocation
    List<Object> args = createParameters(jobClient, job, workerValue.getMethodInfo().getParameters());

    try {
      Object result = workerValue.getMethodInfo().invoke(args.toArray());
      // normal exceptions are handled by JobRunnableFactory
      // (https://github.com/camunda-cloud/zeebe/blob/develop/clients/java/src/main/java/io/camunda/zeebe/client/impl/worker/JobRunnableFactory.java#L45)
      // which leads to retrying
      if (workerValue.isAutoComplete()) {
        CommandWrapper command = new CommandWrapper(
          createCompleteCommand(jobClient, job, result),
          job,
          commandExceptionHandlingStrategy);
        command.executeAsync();
      }
    }
    catch (ZeebeBpmnError bpmnError) {
      CommandWrapper command = new CommandWrapper(
        createThrowErrorCommand(jobClient, job, bpmnError),
        job,
        commandExceptionHandlingStrategy);
      command.executeAsync();
    }
  }

  private List<Object> createParameters(JobClient jobClient, ActivatedJob job, List<ParameterInfo> parameters) {
    List<Object> args = new ArrayList<>();
    for (ParameterInfo param : parameters) {
      Object arg = null; // parameter default null
      Class<?> clazz = param.getParameterInfo().getType();

      if (JobClient.class.isAssignableFrom(clazz)) {
        arg = jobClient;
      } else if (ActivatedJob.class.isAssignableFrom(clazz)) {
        arg = job;
      } else if (param.getParameterInfo().isAnnotationPresent(ZeebeVariable.class)) {
        try {
          // TODO make this work for complex types as well
          arg = clazz.cast(job.getVariablesAsMap().get(param.getParameterName()));
        }
        catch (ClassCastException ex) {
          throw new RuntimeException("Cannot assign process variable '" + param.getParameterName() + "' to parameter, invalid type found: " + ex.getMessage());
        }
      } else if (param.getParameterInfo().isAnnotationPresent(ZeebeVariablesAsType.class)) {
        try {
          arg = job.getVariablesAsType(clazz);
        } catch (RuntimeException e) {
          throw new RuntimeException("Cannot assign process variables to type '" + clazz.getName() + "', cause is: " + e.getMessage(), e);
        }
      }
      args.add(arg);
    }
    return args;
  }

  public FinalCommandStep createCompleteCommand(JobClient jobClient, ActivatedJob job, Object result) {
    CompleteJobCommandStep1 completeCommand = jobClient.newCompleteCommand(job.getKey());
    if (result != null) {
      if (result.getClass().isAssignableFrom(Map.class)) {
        completeCommand = completeCommand.variables((Map) result);
      } else if (result.getClass().isAssignableFrom(String.class)) {
        completeCommand = completeCommand.variables((String)result);
      } else if (result.getClass().isAssignableFrom(InputStream.class)) {
        completeCommand = completeCommand.variables((InputStream)result);
      } else {
        completeCommand = completeCommand.variables(result);
      }
    }
    return completeCommand;
  }

  private FinalCommandStep<Void> createThrowErrorCommand(JobClient jobClient, ActivatedJob job, ZeebeBpmnError bpmnError) {
    FinalCommandStep<Void> command = jobClient.newThrowErrorCommand(job.getKey()) // TODO: PR for taking a job only in command chain
      .errorCode(bpmnError.getErrorCode())
      .errorMessage(bpmnError.getErrorMessage());
    return command;
  }



}
