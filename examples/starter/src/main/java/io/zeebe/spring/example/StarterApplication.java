package io.zeebe.spring.example;

import io.zeebe.client.WorkflowsClient;
import io.zeebe.client.event.WorkflowInstanceEvent;
import io.zeebe.spring.client.annotation.EnableZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class StarterApplication implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(StarterApplication.class);

    public static void main(String... args) {
        SpringApplication.run(StarterApplication.class, args);
    }

    @Autowired
    private WorkflowsClient workflows;

    @Override
    public void run(String... strings) throws Exception {
        WorkflowInstanceEvent event = workflows
                .create("default-topic")
                .bpmnProcessId("demoProcess")
                .payload("{\"a\": \"b\"}")
                .execute();

        log.info("started: {} {}", event.getActivityId(), event.getPayload());

    }
}
