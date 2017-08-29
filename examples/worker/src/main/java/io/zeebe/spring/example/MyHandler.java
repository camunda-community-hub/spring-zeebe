package io.zeebe.spring.example;

import io.zeebe.client.TasksClient;
import io.zeebe.client.event.TaskEvent;
import io.zeebe.client.task.TaskHandler;
import io.zeebe.spring.client.annotation.ZeebeTaskSubscription;
import org.springframework.stereotype.Component;

@ZeebeTaskSubscription(
        topicName = "default-topic",
        lockOwner = "worker-1",
        lockTime = 10000,
        taskType = "foo"
)
@Component
public class MyHandler implements TaskHandler {

    @Override
    public void handle(TasksClient client, TaskEvent task) {
        System.out.println(String.format(">>> [type: %s, key: %s, lockExpirationTime: %s]\n[headers: %s]\n[payload: %s]\n===",
                task.getType(),
                task.getMetadata().getKey(),
                task.getLockExpirationTime().toString(),
                task.getHeaders(),
                task.getPayload()));

        client.complete(task).withoutPayload();
    }
}
