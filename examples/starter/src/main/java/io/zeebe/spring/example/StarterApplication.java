package io.zeebe.spring.example;

import io.zeebe.client.WorkflowsClient;
import io.zeebe.client.event.WorkflowInstanceEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.config.SpringZeebeClient;
import io.zeebe.spring.client.event.ClientStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(
        topicName = "default-topic",
        classPathResource = "demoProcess.bpmn"
)
@Slf4j
public class StarterApplication {

    public static void main(String... args) {
        SpringApplication.run(StarterApplication.class, args);
    }

    @Autowired
    private SpringZeebeClient client;

    @Scheduled(fixedDelay = 15000L)
    public void startProcesses() throws Exception {
        if (!client.isRunning()) {
            return;
        }
        WorkflowInstanceEvent event = client.workflows()
                .create("default-topic")
                .bpmnProcessId("demoProcess")
                .payload("{\"a\": \""+ UUID.randomUUID().toString() + "\"}")
                .execute();

        log.info("started: {} {}", event.getActivityId(), event.getPayload());
    }
}
