package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Slf4j
public class WorkerApplication {

  public static void main(final String... args) {
    SpringApplication.run(WorkerApplication.class, args);
  }

  private static void logJob(final ActivatedJob job, String a) {
    log.info(
      "complete job\n>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variable parameter: {}\n[variables: {}]",
      job.getType(),
      job.getKey(),
      job.getElementId(),
      job.getProcessInstanceKey(),
      Instant.ofEpochMilli(job.getDeadline()),
      job.getCustomHeaders(),
      a,
      job.getVariables());
  }

  @ZeebeWorker(type = "foo")
  public void handleFooJob(final JobClient client, final ActivatedJob job) {
    logJob(job, null);
    client.newCompleteCommand(job.getKey()).variables("{\"foo\": 1}").send().whenComplete((result, exception) -> {
      if (exception == null) {
        log.info("Completed job successful");
      } else {
        log.error("Failed to complete job", exception);
      }
    });
  }

  @ZeebeWorker(type = "bar", fetchVariables = "bar", autoComplete = true) // Variable "foo" gets renamed to "bar" by IO mapping in the process
  public Map<String, Object> handleBarJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String a) {
    logJob(job, a);
    // Done by auto complete: client.newCompleteCommand(job.getKey()).send()
    return Collections.singletonMap("someResult", "42");
  }

  @ZeebeWorker(type = "fail", autoComplete = true)
  public void handleFailingJob(final JobClient client, final ActivatedJob job) {
    logJob(job, null);
    throw new ZeebeBpmnError("DOESNT_WORK", "This will actually never work :-)");
  }
}
