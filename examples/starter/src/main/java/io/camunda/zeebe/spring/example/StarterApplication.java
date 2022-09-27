package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.annotation.Deployment;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@Deployment(resources = "classpath:demoProcess.bpmn")
public class StarterApplication {

  private static Logger log = LoggerFactory.getLogger(StarterApplication.class);

  public static void main(final String... args) {
    SpringApplication.run(StarterApplication.class, args);
  }

  @Autowired
  private ZeebeClientLifecycle client;

  @Scheduled(fixedRate = 5000L)
  public void startProcesses() {
    if (!client.isRunning()) {
      return;
    }

    final ProcessInstanceEvent event =
      client
        .newCreateInstanceCommand()
        .bpmnProcessId("demoProcess")
        .latestVersion()
        .variables("{\"a\": \"" + UUID.randomUUID().toString() + "\",\"b\": \"" + new Date().toString() + "\"}")
        .send()
        .join();

    log.info("started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
      event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());
  }
}
