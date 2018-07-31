package io.zeebe.spring.example;

import io.zeebe.client.api.events.WorkflowInstanceEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.config.SpringZeebeClient;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(classPathResource = "demoProcess.bpmn")
@Slf4j
public class StarterApplication {

  public static void main(final String... args) {
    SpringApplication.run(StarterApplication.class, args);
  }

  @Autowired private SpringZeebeClient client;

  @Value("${zeebe.topic}")
  private String topic;

  @Scheduled(fixedDelay = 15000L)
  public void startProcesses() {
    if (!client.isRunning()) {
      return;
    }
    final WorkflowInstanceEvent event =
        client
            .topicClient()
            .workflowClient()
            .newCreateInstanceCommand()
            .bpmnProcessId("demoProcess")
            .latestVersion()
            .payload("{\"a\": \"" + UUID.randomUUID().toString() + "\"}")
            .send()
            .join();

    log.info("started: {} {}", event.getActivityId(), event.getPayload());
  }
}
