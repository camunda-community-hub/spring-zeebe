package io.camunda.zeebe.spring.client.jobhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretStore;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.Loggers;
import io.camunda.zeebe.spring.client.annotation.ZeebeCustomHeaders;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariablesAsType;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import io.camunda.zeebe.spring.client.jobhandling.copy.JobHandlerContext;
import io.camunda.zeebe.spring.client.jobhandling.copy.OutboundConnectorFunctionInvoker;
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
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private ZeebeWorkerValue workerValue;
  private DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private SecretStore secretStore;

  // This handler can either invoke any normal worker (JobHandler, @ZeebeWorker) or an outbound connector function
  private OutboundConnectorFunction outboundConnectorFunction;

  public JobHandlerInvokingSpringBeans(ZeebeWorkerValue workerValue, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
  }

  public JobHandlerInvokingSpringBeans(ZeebeWorkerValue workerValue, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy, SecretStore secretStore, OutboundConnectorFunction outboundConnectorFunction) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.secretStore = secretStore;
    this.outboundConnectorFunction = outboundConnectorFunction;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    try {
      Object result = null;
      if (outboundConnectorFunction!=null) {
        JobHandlerContext jobHandlerContext = createJobHandlerContext(job);
        result = new OutboundConnectorFunctionInvoker().execute(
          outboundConnectorFunction,
          jobHandlerContext,
          job);
      } else { // "normal" @JobWorker
        // TODO: Figuring out parameters and assignments could probably also done only once in the beginning to save some computing time on each invocation
        List<Object> args = createParameters(jobClient, job, workerValue.getMethodInfo().getParameters());
        result = workerValue.getMethodInfo().invoke(args.toArray());
      }

      // normal exceptions are handled by JobRunnableFactory
      // (https://github.com/camunda-cloud/zeebe/blob/develop/clients/java/src/main/java/io/camunda/zeebe/client/impl/worker/JobRunnableFactory.java#L45)
      // which leads to retrying
      if (workerValue.getAutoComplete()) {
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
        String paramName =param.getParameterName();
        Object variableValue = job.getVariablesAsMap().get(paramName);
        try {
          if (variableValue != null && !clazz.isInstance(variableValue)) {
            arg = OBJECT_MAPPER.convertValue(variableValue, param.getParameterInfo().getType());
          } else {
            arg = clazz.cast(variableValue);
          }
        }
        catch (ClassCastException | IllegalArgumentException ex) {
          throw new RuntimeException("Cannot assign process variable '" + paramName + "' to parameter when executing job '"+job.getType()+"', invalid type found: " + ex.getMessage());
        }
      } else if (param.getParameterInfo().isAnnotationPresent(ZeebeVariablesAsType.class)) {
        try {
          arg = job.getVariablesAsType(clazz);
        } catch (RuntimeException e) {
          throw new RuntimeException("Cannot assign process variables to type '" + clazz.getName() + "' when executing job '"+job.getType()+"', cause is: " + e.getMessage(), e);
        }
      } else if (param.getParameterInfo().isAnnotationPresent(ZeebeCustomHeaders.class)) {
        try {
          arg = job.getCustomHeaders();
        } catch (RuntimeException e) {
          throw new RuntimeException("Cannot assign headers '" + param.getParameterName() + "' to parameter when executing job '"+job.getType()+"', cause is: " + e.getMessage(), e);
        }
      }
      args.add(arg);
    }
    return args;
  }

  protected JobHandlerContext createJobHandlerContext(ActivatedJob job) {
    return new JobHandlerContext(job, secretStore);
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
