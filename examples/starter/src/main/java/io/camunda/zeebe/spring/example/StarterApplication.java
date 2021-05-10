package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(classPathResources = "demoProcess.bpmn")
@Slf4j
public class StarterApplication {

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
        .variables("{\"a\": \"" + UUID.randomUUID().toString() + "\"}")
        .send()
        .join();

    log.info("started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
      event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());
  }
}
