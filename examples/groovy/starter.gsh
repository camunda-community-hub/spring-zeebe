#!/usr/bin/env groovy
package io.zeebe.spring.groovy

import groovy.util.logging.Slf4j
import io.zeebe.client.api.response.WorkflowInstanceEvent
import io.zeebe.spring.client.EnableZeebeClient
import io.zeebe.spring.client.ZeebeClientLifecycle
import io.zeebe.spring.client.annotation.ZeebeDeployment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Grab("io.zeebe.spring:spring-zeebe-starter:0.7.0-SNAPSHOT")
@Slf4j
@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(classPathResource = "demoProcess.bpmn")
class Application {

  @Autowired
  private ZeebeClientLifecycle client;


  @Scheduled(fixedRate = 5000L)
  void startWorkflow() {
    if (!client.isRunning()) {
      return;
    }

    final WorkflowInstanceEvent event =
      client
        .newCreateInstanceCommand()
        .bpmnProcessId("demoProcess")
        .latestVersion()
        .variables("{\"a\": \"" + UUID.randomUUID().toString() + "\"}")
        .send()
        .join();

    log.info("started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
      event.getWorkflowKey(), event.getBpmnProcessId(), event.getVersion(), event.getWorkflowInstanceKey());
  }

}

SpringApplication.run(Application, args)
