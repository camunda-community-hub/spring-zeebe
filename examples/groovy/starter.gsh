#!/usr/bin/env groovy
package io.zeebe.spring.groovy

import groovy.util.logging.Slf4j
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent
import io.zeebe.spring.client.EnableZeebeClient
import io.zeebe.spring.client.ZeebeClientLifecycle
import io.zeebe.spring.client.annotation.ZeebeDeployment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Grab("io.zeebe.spring:spring-zeebe-starter:1.0.0-SNAPSHOT")
@Slf4j
@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(classPathResources = "demoProcess.bpmn")
class Application {

  @Autowired
  private ZeebeClientLifecycle client;


  @Scheduled(fixedRate = 5000L)
  void startProcess() {
    if (!client.isRunning()) {
      return;
    }

    final ProcessInstanceEvent event =
      client
        .newCreateInstanceCommand()
        .bpmnProcessId("demoProcess")
        .latestVersion()
        .variables("{\"a\": \"" + UUID.randomUUID().toString() + "\"}")
        .send()
        .join();

    log.info("started instance for processDefinitionKey='{}', bpmnProcessId='{}', version='{}' with processInstanceKey='{}'",
      event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());
  }

}

SpringApplication.run(Application, args)
