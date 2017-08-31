package io.zeebe.spring.example;

import io.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class WorkerApplication {

    public static void main(String... args) {
        SpringApplication.run(WorkerApplication.class, args);
    }


}
