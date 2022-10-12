package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class WorkerApplication {

  private static Logger log = LoggerFactory.getLogger(WorkerApplication.class);

  public static void main(final String... args) {
    SpringApplication.run(WorkerApplication.class, args);
  }

  private static void logJob(final ActivatedJob job, Object parameterValue) {
    log.info(
      "complete job\n>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variable parameter: {}\n[variables: {}]",
      job.getType(),
      job.getKey(),
      job.getElementId(),
      job.getProcessInstanceKey(),
      Instant.ofEpochMilli(job.getDeadline()),
      job.getCustomHeaders(),
      parameterValue,
      job.getVariables());
  }

  @JobWorker(type = "foo")
  public void handleFooJob(final ActivatedJob job) {
    logJob(job, null);
  }

  @JobWorker()
  public Map<String, Object> bar(final ActivatedJob job, @Variable String a) {
    logJob(job, a);
    return Collections.singletonMap("someResult", "42");
  }

  @JobWorker(type = "fail", autoComplete = true, fetchAllVariables = true)
  public void handleFailingJob(final JobClient client, final ActivatedJob job, @Variable String someResult) {
    logJob(job, someResult);
    throw new ZeebeBpmnError("DOESNT_WORK", "This will actually never work :-)");
  }
}
