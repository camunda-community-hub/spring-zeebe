#!/usr/bin/env groovy
package io.zeebe.spring.groovy

import groovy.util.logging.Slf4j
import io.zeebe.client.api.worker.JobClient
import io.zeebe.client.api.response.ActivatedJob
import io.zeebe.spring.client.EnableZeebeClient
import io.zeebe.spring.client.annotation.ZeebeWorker
import java.time.Instant;
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@Grab("io.zeebe.spring:spring-zeebe-starter:0.7.0-SNAPSHOT")
@Slf4j
@SpringBootApplication
@EnableZeebeClient
class Application {

  private static void logJob(final ActivatedJob job) {
    log.info(
      "complete job\n>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variables: {}]",
      job.getType(),
      job.getKey(),
      job.getElementId(),
      job.getWorkflowInstanceKey(),
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
