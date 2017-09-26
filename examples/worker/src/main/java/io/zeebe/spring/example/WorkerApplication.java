package io.zeebe.spring.example;

import io.zeebe.client.TasksClient;
import io.zeebe.client.event.EventMetadata;
import io.zeebe.client.event.GeneralEvent;
import io.zeebe.client.event.TaskEvent;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Slf4j
public class WorkerApplication  {

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
        client.complete(task).withoutPayload().execute();
    }

    /**
     * logs every event.
     *
     * @param event
     */
    @ZeebeTopicListener
    public void logEvents(GeneralEvent event) {
        final EventMetadata metadata = event.getMetadata();

        log.info(String.format(">>> [topic: %d, position: %d, key: %d, type: %s]\n%s\n===",
                metadata.getPartitionId(),
                metadata.getPosition(),
                metadata.getKey(),
                metadata.getType(),
                event.getJson()));
    }

    @ZeebeTaskListener(taskType = "foo")
    public void handleTaskA(final TasksClient client, final TaskEvent task) {
        logTask(task);
        completeTask(client, task);
    }

    @ZeebeTaskListener(taskType = "bar")
    public void handleTaskB(final TasksClient client, final TaskEvent task) {
        logTask(task);
        completeTask(client, task);
    }



}
