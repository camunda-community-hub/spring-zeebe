package io.camunda.zeebe.spring.client.config.processor;

import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.Loggers;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZeebeWorkerSpringJobHandler implements JobHandler {

  private static final Logger LOG = Loggers.JOB_WORKER_LOGGER;
  private ZeebeWorkerValue workerValue;

  public ZeebeWorkerSpringJobHandler(ZeebeWorkerValue workerValue) {
    this.workerValue = workerValue;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    List<ParameterInfo> parameters = workerValue.getBeanInfo().getParameters();
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
          arg = clazz.cast(job.getVariablesAsMap().get(param.getParameterName()));
        }
        catch (ClassCastException ex) {
          throw new RuntimeException("Cannot assign process variable '" + param.getParameterName() + "' to parameter, invalid type found: " + ex.getMessage());
        }
      }
      args.add(arg);
    }

    try {
      Object result = workerValue.getBeanInfo().invoke(args.toArray());
      if (workerValue.isAutoComplete()) {
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
        completeCommand.send().exceptionally(throwable -> {
          throw new RuntimeException("Could not complete job " + job + " to Zeebe due to error: " + throwable.getMessage(), throwable); // probably do a retry once?
        });
      }
    }
    catch (Throwable throwable) {
      if (workerValue.isAutoComplete()) {
        handleJobCompletionException(jobClient, job, throwable);
      } else {
        throw throwable;
      }
    }

  }

  public void handleJobCompletionException(JobClient jobClient, ActivatedJob job, Throwable throwable) {
    if (throwable.getClass().isAssignableFrom(ZeebeBpmnError.class)) {
      ZeebeBpmnError error = (ZeebeBpmnError) throwable;
      jobClient.newThrowErrorCommand(job.getKey()) // TODO: PR for taking a job only in command chain
        .errorCode(error.getErrorCode())
        .errorMessage(error.getErrorMessage())
        .send()
        .exceptionally(t -> {
          throw new RuntimeException("Could not send BPMN error from job " + job + " to Zeebe due to error: " + t.getMessage(), t);  // probably do a retry once?
        });
    } else {
      // comparable to https://github.com/camunda-cloud/zeebe/blob/develop/clients/java/src/main/java/io/camunda/zeebe/client/impl/worker/JobRunnableFactory.java#L45
      LOG.warn(
        "Worker {} failed to handle job with key {} of type {}, sending fail command to broker",
        job.getWorker(),
        job.getKey(),
        job.getType(),
        throwable);
      final StringWriter stringWriter = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(stringWriter);
      throwable.printStackTrace(printWriter);
      final String message = stringWriter.toString();

      jobClient
        .newFailCommand(job.getKey())
        .retries(job.getRetries() - 1)
        .errorMessage(message)
        .send()
        .exceptionally(t -> {
          throw new RuntimeException("Could not send error from job " + job + " to Zeebe due to error: " + t.getMessage(), t);  // probably do a retry once?
        });
    }
  }

}
