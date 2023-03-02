package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Deployment(resources = "classpath:demoProcess.bpmn")
public class ExampleApplication {

  public static void main(final String... args) {
    SpringApplication.run(ExampleApplication.class, args);
  }

}
