package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.event.TaskReceivedEvent;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * For clients who don't wish to use @JobWorker annotation and may want to dynamically (programmatically) register workers based on logic etc.
 *
 * @author Sai.
 */
public class DynamicJobWorkerRegistrar {

  /**
   * Application context that's already in scope.
   */
  private final ApplicationContext applicationContext;
  private String jobType;
  private String jobName;
  private Consumer<TaskReceivedEvent> taskReceiver;
  private boolean emitSpringEvent;
  private JobHandler mockJobHandler;

  private DynamicJobWorkerRegistrar(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public static DynamicJobWorkerRegistrar newInstance(final ApplicationContext applicationContext) {
    Objects.requireNonNull(applicationContext, "Application context is null. It is required to be injected correctly.");
    return new DynamicJobWorkerRegistrar(applicationContext);
  }

  public DynamicJobWorkerRegistrar withJobType(final String jobType) {
    this.jobType = jobType;
    return this;
  }

  public DynamicJobWorkerRegistrar withJobName(final String jobName) {
    this.jobName = jobName;
    return this;
  }

  public DynamicJobWorkerRegistrar withHandler(final Consumer<TaskReceivedEvent> taskReceiver) {
    Objects.requireNonNull(taskReceiver, "TaskReceiver cannot ne bull. A non null value is required.");
    this.taskReceiver = taskReceiver;
    return this;
  }

  public DynamicJobWorkerRegistrar emitSpringApplicationEvent(final boolean emitSpringEvent) {
    this.emitSpringEvent = emitSpringEvent;
    return this;
  }

  // Used only for unit testing.
  JobHandler mockJobHandler(final ActivatedJob mockJob) {
    this.mockJobHandler = (a, b) -> handle(mockJob);
    return this.mockJobHandler;
  }

  public void build() {
    ZeebeClient zeebeClient = applicationContext.getBean(ZeebeClient.class);
    Objects.requireNonNull(jobType, "JobType must be a non null value.");
    Objects.requireNonNull(jobName, "JobName must be a non null value.");
    Objects.requireNonNull(zeebeClient, "Zeebe client is null. It is required to be injected correctly.");
    if (!emitSpringEvent && taskReceiver == null) {
      throw new IllegalArgumentException("Either emitSpringEvent should be set to true or a handler must be supplied to handle the Task.");
    } else if (emitSpringEvent && taskReceiver != null) {
      throw new IllegalArgumentException("Only one of emitSpringEvent should be set to true or a handler must be supplied to handle the Task.");
    } else {
      try {
        JobHandler jobHandler = mockJobHandler == null ? (client, job) -> handle(job) : mockJobHandler;
        zeebeClient.newWorker()
          .jobType(this.jobType)
          .handler(jobHandler)
          .name(jobName)
          .open();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  private void handle(final ActivatedJob job) {
    TaskReceivedEvent taskReceivedEvent = getTaskReceivedEvent(job);
    if (emitSpringEvent) {
      applicationContext.publishEvent(taskReceivedEvent);
    } else {
      taskReceiver.accept(taskReceivedEvent);
    }
  }

  private TaskReceivedEvent getTaskReceivedEvent(final ActivatedJob job) {
    return new TaskReceivedEvent(
      job.getKey() + "",
      job.getBpmnProcessId(),
      job.getProcessDefinitionVersion() + "",
      job.getElementId(),
      job.getWorker(),
      job.getVariablesAsMap(),
      job.getCustomHeaders()
    );
  }


}
