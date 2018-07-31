package io.zeebe.spring.example;

import io.zeebe.client.api.clients.JobClient;
import io.zeebe.client.api.events.JobEvent;
import io.zeebe.client.api.record.Record;
import io.zeebe.client.api.record.RecordMetadata;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Slf4j
public class WorkerApplication {

  public static void main(final String... args) {
    SpringApplication.run(WorkerApplication.class, args);
  }

  private static void logTask(final JobEvent task) {
    log.info(
        "complete task\n>>> [type: {}, key: {}]\n[headers: {}]\n[payload: {}]\n===",
        task.getType(),
        task.getMetadata().getKey(),
        task.getHeaders(),
        task.getPayload());
  }

  /** logs every event. */
  @ZeebeTopicListener(name = "log-events")
  public void logEvents(final Record event) {
    final RecordMetadata metadata = event.getMetadata();

    log.info(
        String.format(
            ">>> [topic: %d, position: %d, key: %d, type: %s]\n%s\n===",
            metadata.getPartitionId(), metadata.getPosition(), metadata.getKey()));
  }

  @ZeebeWorker(taskType = "foo")
  public void handleTaskA(final JobClient client, final JobEvent task) {
    logTask(task);
    client.newCompleteCommand(task).payload("{\"foo\": 1}").send();
  }

  @ZeebeWorker(taskType = "bar")
  public void handleTaskB(final JobClient client, final JobEvent task) {
    logTask(task);
    client.newCompleteCommand(task).send();
  }
}
