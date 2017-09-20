package io.zeebe.spring.example;

import io.zeebe.client.TasksClient;
import io.zeebe.client.event.TaskEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Slf4j
public class WorkerApplication implements CommandLineRunner {

    public static void main(String... args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    private static void logTask(TaskEvent task) {
        log.info(">>> [type: {}, key: {}, lockExpirationTime: {}]\n[headers: {}]\n[payload: {}]\n===",
                task.getType(),
                task.getMetadata().getKey(),
                task.getLockExpirationTime().toString(),
                task.getHeaders(),
                task.getPayload());
    }

    private static void completeTask(final TasksClient client, final TaskEvent task) {
        client.complete(task).withoutPayload();
    }

    @ZeebeTaskListener(
            topicName = "default-topic",
            lockOwner = "worker-1",
            taskType = "taskA"
    )
    public void handleTaskA(final TasksClient client, final TaskEvent task) {
        logTask(task);
        completeTask(client, task);
    }

    @ZeebeTaskListener(
            topicName = "default-topic",
            lockOwner = "worker-1",
            taskType = "taskB"
    )
    public void handleTaskB(final TasksClient client, final TaskEvent task) {
        logTask(task);
        completeTask(client, task);
    }

    @ZeebeTaskListener(
            topicName = "default-topic",
            lockOwner = "worker-1",
            taskType = "taskC"
    )
    public void handleTaskC(final TasksClient client, final TaskEvent task) {
        logTask(task);
        completeTask(client, task);
    }

    @Autowired
    private SpringZeebeClient client;

    @Override
    public void run(String... strings) throws Exception {
        client.tasks().newTaskSubscription("default-topic")
                .lockOwner("me")
                .handler((tasksClient, taskEvent) -> {
                    log.info("{}", taskEvent);
                    tasksClient.complete(taskEvent).withoutPayload();
                })
                .lockTime(10000L)
                .taskFetchSize(32)
                .taskType("taskA")
                .open();

        client.tasks().newTaskSubscription("default-topic")
                .lockOwner("me")
                .handler((tasksClient, taskEvent) -> {
                    log.info("{}", taskEvent);
                    tasksClient.complete(taskEvent).withoutPayload();
                })
                .lockTime(10000L)
                .taskFetchSize(32)
                .taskType("taskB")
                .open();

        client.tasks().newTaskSubscription("default-topic")
                .lockOwner("me")
                .handler((tasksClient, taskEvent) -> {
                    log.info("{}", taskEvent);
                    tasksClient.complete(taskEvent).withoutPayload();
                })
                .lockTime(10000L)
                .taskFetchSize(32)
                .taskType("taskC")
                .open();
    }
}
