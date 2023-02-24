package io.camunda.zeebe.spring.client.jobhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.connector.runtime.util.outbound.OutboundConnectorFactory;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.impl.Loggers;
import io.camunda.zeebe.spring.client.annotation.*;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Zeebe JobHandler that invokes a Spring bean
 */
public class JobHandlerInvokingSpringBeans implements JobHandler {

  private static final Logger LOG = Loggers.JOB_WORKER_LOGGER;
  private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

  private final ZeebeWorkerValue workerValue;
  private final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final SecretProvider secretProvider;
  private final JsonMapper jsonMapper;
  private final MetricsRecorder metricsRecorder;
  private final OutboundConnectorFactory outboundConnectorFactory;

  // This handler can either invoke any normal worker (JobHandler, @ZeebeWorker) or an outbound connector function
  private OutboundConnectorConfiguration outboundConnectorConfiguration;

  public JobHandlerInvokingSpringBeans(ZeebeWorkerValue workerValue,
                                       CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
                                       JsonMapper jsonMapper,
                                       MetricsRecorder metricsRecorder) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.jsonMapper = jsonMapper;
    this.metricsRecorder = metricsRecorder;
    this.secretProvider = null;
    this.outboundConnectorFactory = null;
  }

  public JobHandlerInvokingSpringBeans(ZeebeWorkerValue workerValue,
                                       CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
                                       SecretProvider secretProvider,
                                       OutboundConnectorConfiguration connectorConfiguration,
                                       OutboundConnectorFactory outboundConnectorFactory,
                                       JsonMapper jsonMapper,
                                       MetricsRecorder metricsRecorder) {
    this.workerValue = workerValue;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.secretProvider = secretProvider;
    this.outboundConnectorConfiguration = connectorConfiguration;
    this.jsonMapper = jsonMapper;
    this.metricsRecorder = metricsRecorder;
    this.outboundConnectorFactory = outboundConnectorFactory;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    if (outboundConnectorConfiguration!=null) {
      handleConnectorJobWorker(jobClient, job);
    } else { // "normal" @JobWorker
      handleRegularJobWorker(jobClient, job);
    }
  }

  private void handleConnectorJobWorker(JobClient jobClient, ActivatedJob job) {
    if (outboundConnectorFactory == null) {
      throw new IllegalStateException("Connector factory is not initialized");
    }
    OutboundConnectorFunction connectorFunction = outboundConnectorFactory
      .getInstance(outboundConnectorConfiguration.getType());

    LOG.trace("Handle {} and execute connector {}", job, outboundConnectorConfiguration);

    metricsRecorder.executeWithTimer(job.getType(), () -> new SpringConnectorJobHandler(
      outboundConnectorConfiguration,
      connectorFunction,
      secretProvider,
      commandExceptionHandlingStrategy,
      metricsRecorder
    ).handle(jobClient, job));
  }

  private void handleRegularJobWorker(JobClient jobClient, ActivatedJob job) throws Exception {
    // TODO: Figuring out parameters and assignments could probably also done only once in the beginning to save some computing time on each invocation
    List<Object> args = createParameters(jobClient, job, workerValue.getMethodInfo().getParameters());
    LOG.trace("Handle {} and invoke worker {}", job, workerValue);
    try {
      metricsRecorder.increase(MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_ACTIVATED, job.getType());
      Object result = null;
      try {
        result = workerValue.getMethodInfo().invoke(args.toArray());
      } catch (Throwable t) {
        metricsRecorder.increase(MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_FAILED, job.getType());
        // normal exceptions are handled by JobRunnableFactory
        // (https://github.com/camunda-cloud/zeebe/blob/develop/clients/java/src/main/java/io/camunda/zeebe/client/impl/worker/JobRunnableFactory.java#L45)
        // which leads to retrying
        throw t;
      }

      if (workerValue.getAutoComplete()) {
        LOG.trace("Auto completing {}", job);
        // TODO: We should probably move the metrics recording to the callback of a successful command execution to avoid wrong counts
        metricsRecorder.increase(MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_COMPLETED, job.getType());
        CommandWrapper command = new CommandWrapper(
          createCompleteCommand(jobClient, job, result),
          job,
          commandExceptionHandlingStrategy);
        command.executeAsync();
      }
    }
    catch (ZeebeBpmnError bpmnError) {
      LOG.trace("Catched BPMN error on {}", job);
      // TODO: We should probably move the metrics recording to the callback of a successful command execution to avoid wrong counts
      metricsRecorder.increase(MetricsRecorder.METRIC_NAME_JOB, MetricsRecorder.ACTION_BPMN_ERROR, job.getType());
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
      } else if (param.getParameterInfo().isAnnotationPresent(Variable.class) || param.getParameterInfo().isAnnotationPresent(ZeebeVariable.class)) {
        String paramName = param.getParameterName();
        Object variableValue = job.getVariablesAsMap().get(paramName);
        try {
          arg = mapZeebeVariable(variableValue, param.getParameterInfo().getType());
        }
        catch (ClassCastException | IllegalArgumentException ex) {
          throw new RuntimeException("Cannot assign process variable '" + paramName + "' to parameter when executing job '"+job.getType()+"', invalid type found: " + ex.getMessage());
        }
      } else if (param.getParameterInfo().isAnnotationPresent(VariablesAsType.class) || param.getParameterInfo().isAnnotationPresent(ZeebeVariablesAsType.class)) {
        try {
          arg = job.getVariablesAsType(clazz);
        } catch (RuntimeException e) {
          throw new RuntimeException("Cannot assign process variables to type '" + clazz.getName() + "' when executing job '"+job.getType()+"', cause is: " + e.getMessage(), e);
        }
      } else if (param.getParameterInfo().isAnnotationPresent(CustomHeaders.class) || param.getParameterInfo().isAnnotationPresent(ZeebeCustomHeaders.class)) {
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

  public static FinalCommandStep createCompleteCommand(JobClient jobClient, ActivatedJob job, Object result) {
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

  private <T>T mapZeebeVariable(Object toMap, Class<T> clazz) {
    if (toMap != null && !clazz.isInstance(toMap)) {
      if (jsonMapper != null) {
        return jsonMapper.fromJson(jsonMapper.toJson(toMap), clazz);
      }
      return DEFAULT_OBJECT_MAPPER.convertValue(toMap, clazz);
    } else {
      return clazz.cast(toMap);
    }
  }

}
