package io.camunda.zeebe.spring.client.config.processor;

import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.Loggers;
import io.camunda.zeebe.spring.client.annotation.ZeebeTypedVariables;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.exception.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZeebeWorkerSpringJobHandler implements JobHandler {

  private static final Logger LOG = Loggers.JOB_WORKER_LOGGER;
  private ZeebeWorkerValue workerValue;
  private DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;

  public ZeebeWorkerSpringJobHandler(ZeebeWorkerValue workerValue, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    // TODO: Figuring out parameters and assignments could probably also done only once in the beginning to save some computing time on each invocation
    List<Object> args = createParameters(jobClient, job, workerValue.getBeanInfo().getParameters());

    try {
      Object result = workerValue.getBeanInfo().invoke(args.toArray());
      // normal exceptions are handled by JobRunnableFactory
      // (https://github.com/camunda-cloud/zeebe/blob/develop/clients/java/src/main/java/io/camunda/zeebe/client/impl/worker/JobRunnableFactory.java#L45)
      // which leads to retrying
      if (workerValue.isAutoComplete()) {
        final FinalCommandStep<Void> command = createCompleteCommand(jobClient, job, result);
        command.send().exceptionally(t -> {
          commandExceptionHandlingStrategy.handleCommandError(jobClient, job, command, t);
          return null;
        });
      }
    }
    catch (ZeebeBpmnError bpmnError) {
      handleBpmnError(jobClient, job, bpmnError);
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
          String variableName = param.getParameterInfo().getAnnotation(ZeebeVariable.class).value();

          if (StringUtils.isEmpty(variableName.trim())) {
            variableName = param.getParameterName();
          }
          arg = clazz.cast(job.getVariablesAsMap().get(variableName));
        } catch (ClassCastException ex) {
          throw new RuntimeException("Cannot assign process variable '" + param.getParameterName() + "' to parameter, invalid type found: " + ex.getMessage());
        }
      } else if (param.getParameterInfo().isAnnotationPresent(ZeebeTypedVariables.class)) {
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

  public void handleBpmnError(JobClient jobClient, ActivatedJob job,  ZeebeBpmnError bpmnError) {
    FinalCommandStep<Void> command = jobClient.newThrowErrorCommand(job.getKey()) // TODO: PR for taking a job only in command chain
      .errorCode(bpmnError.getErrorCode())
      .errorMessage(bpmnError.getErrorMessage());

    command.send()
      .exceptionally(t -> {
        commandExceptionHandlingStrategy.handleCommandError(jobClient, job, command, t);
        return null;
      });
  }


}
