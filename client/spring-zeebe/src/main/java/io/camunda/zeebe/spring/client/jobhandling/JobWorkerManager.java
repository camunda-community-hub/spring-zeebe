package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.connector.MetricsRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobWorkerManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final CommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final SecretProvider secretProvider;

  private List<JobWorker> openedWorkers = new ArrayList<>();
  private List<ZeebeWorkerValue> workerValues = new ArrayList<>();
  private JsonMapper jsonMapper;

  public JobWorkerManager(CommandExceptionHandlingStrategy commandExceptionHandlingStrategy,
                          SecretProvider secretProvider,
                          JsonMapper jsonMapper) {
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.secretProvider = secretProvider;
    this.jsonMapper = jsonMapper;
  }

  public JobWorker openWorker(ZeebeClient client, ZeebeWorkerValue zeebeWorkerValue) {
    return openWorker(
      client,
      zeebeWorkerValue,
      new JobHandlerInvokingSpringBeans(zeebeWorkerValue, commandExceptionHandlingStrategy, jsonMapper));
  }

  public JobWorker openWorker(ZeebeClient client, ZeebeWorkerValue zeebeWorkerValue, OutboundConnectorFunction function, MetricsRecorder metricsRecorder) {
    return openWorker(
      client,
      zeebeWorkerValue,
      new JobHandlerInvokingSpringBeans(zeebeWorkerValue, commandExceptionHandlingStrategy, secretProvider, function, jsonMapper, metricsRecorder));
  }

  public JobWorker openWorker(ZeebeClient client, ZeebeWorkerValue zeebeWorkerValue, JobHandler handler) {

	// TODO: Trigger initialization of  worker values and defaults here

    final JobWorkerBuilderStep1.JobWorkerBuilderStep3 builder = client
      .newWorker()
      .jobType(zeebeWorkerValue.getType())
      .handler(handler)
      .name(zeebeWorkerValue.getName());

    if (zeebeWorkerValue.getMaxJobsActive() != null && zeebeWorkerValue.getMaxJobsActive() > 0) {
      builder.maxJobsActive(zeebeWorkerValue.getMaxJobsActive());
    }
    if (zeebeWorkerValue.getTimeout() !=null && zeebeWorkerValue.getTimeout()  > 0) {
      builder.timeout(zeebeWorkerValue.getTimeout());
    }
    if (zeebeWorkerValue.getPollInterval() != null && zeebeWorkerValue.getPollInterval() > 0) {
      builder.pollInterval(Duration.ofMillis(zeebeWorkerValue.getPollInterval()));
    }
    if (zeebeWorkerValue.getRequestTimeout() != null && zeebeWorkerValue.getRequestTimeout() > 0) {
      builder.requestTimeout(Duration.ofSeconds(zeebeWorkerValue.getRequestTimeout()));
    }
    if (zeebeWorkerValue.getFetchVariables() != null && zeebeWorkerValue.getFetchVariables().length > 0) {
      builder.fetchVariables(zeebeWorkerValue.getFetchVariables());
    }

    JobWorker jobWorker = builder.open();
    openedWorkers.add(jobWorker);
    workerValues.add(zeebeWorkerValue);
    LOGGER.info(". Starting Zeebe worker: {}", zeebeWorkerValue);
    return jobWorker;
  }

  public void closeAllOpenWorkers() {
    openedWorkers.forEach(worker -> worker.close());
    openedWorkers = new ArrayList<>();
  }

  public void closeWorker(JobWorker worker) {
    worker.close();
    int i = openedWorkers.indexOf(worker);
    openedWorkers.remove(i);
    workerValues.remove(i);
  }

  public Optional<ZeebeWorkerValue> findJobWorkerConfigByName(String name) {
    return workerValues.stream().filter( worker -> worker.getName().equals(name) ).findFirst();
  }

  public Optional<ZeebeWorkerValue> findJobWorkerConfigByType(String type) {
    return workerValues.stream().filter( worker -> worker.getType().equals(type) ).findFirst();
  }
}
