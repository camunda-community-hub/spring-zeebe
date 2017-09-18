package io.zeebe.spring.example;

import io.zeebe.client.WorkflowsClient;
import io.zeebe.client.event.DeploymentEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableZeebeClient
@Slf4j
@ZeebeDeployment(topicName = "default-topic", classPathResource = "demoProcess.bpmn")
public class DeployerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeployerApplication.class, args);
    }

}
