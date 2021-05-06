#!/usr/bin/env groovy
package io.camunda.spring.groovy

import groovy.util.logging.Slf4j
import io.camunda.zeebe.client.api.worker.JobClient
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.spring.client.EnableZeebeClient
import io.camunda.spring.client.annotation.ZeebeWorker
import java.time.Instant;
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@Grab("io.camunda:spring-zeebe-starter:1.0.0-SNAPSHOT")
@Slf4j
@SpringBootApplication
@EnableZeebeClient
class Application {

  private static void logJob(final ActivatedJob job) {
    log.info(
      "complete job\n>>> [type: {}, key: {}, element: {}, process instance: {}]\n{deadline; {}]\n[headers: {}]\n[variables: {}]",
      job.getType(),
      job.getKey(),
      job.getElementId(),
      job.getProcessInstanceKey(),
      Instant.ofEpochMilli(job.getDeadline()),
      job.getCustomHeaders(),
      job.getVariables());
  }

  @ZeebeWorker(type = "foo", name = "groovy-worker")
  public void handleFooJob(final JobClient client, final ActivatedJob job) {
    logJob(job);
    client.newCompleteCommand(job.getKey()).variables("{\"foo\": 1}").send().join();
  }

  @ZeebeWorker(type = "bar", name = "groovy-worker")
  public void handleBarJob(final JobClient client, final ActivatedJob job) {
    logJob(job);
    client.newCompleteCommand(job.getKey()).send().join();
  }

}

SpringApplication.run(Application, args)
