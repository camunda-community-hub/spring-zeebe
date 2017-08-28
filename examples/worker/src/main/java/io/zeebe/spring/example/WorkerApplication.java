package io.zeebe.spring.example;

import io.zeebe.client.TasksClient;
import io.zeebe.client.task.TaskSubscription;
import io.zeebe.spring.EnableZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@SpringBootApplication
@EnableZeebeClient
public class WorkerApplication {

    public static void main(String... args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    @Autowired
    private TasksClient tasks;



}
