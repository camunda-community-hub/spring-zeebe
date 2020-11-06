package io.zeebe.spring.example;

import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.ZeebeClientLifecycle;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import java.lang.invoke.MethodHandles;
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
@ZeebeDeployment(classPathResources = "demoProcess.bpmn")
public class StarterApplication {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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

    final WorkflowInstanceEvent event =
      client
        .newCreateInstanceCommand()
        .bpmnProcessId("demoProcess")
        .latestVersion()
        .variables("{\"a\": \"" + UUID.randomUUID().toString() + "\"}")
        .send()
        .join();

    LOGGER.info("started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
      event.getWorkflowKey(), event.getBpmnProcessId(), event.getVersion(), event.getWorkflowInstanceKey());
  }
}
