package io.zeebe.spring.example;

import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Slf4j
@ZeebeDeployment(
        topicName = "default-topic",
        classPathResource = "demoProcess.bpmn"
)
public class DeployerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeployerApplication.class, args);
    }

}
