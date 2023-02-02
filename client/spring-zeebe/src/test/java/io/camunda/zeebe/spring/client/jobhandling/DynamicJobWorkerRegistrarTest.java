package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1;
import io.camunda.zeebe.spring.client.event.TaskReceivedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicJobWorkerRegistrarTest {

  @Mock
  private ApplicationContext applicationContext;

  @Mock
  private ZeebeClient zeebeClient;

  @Mock
  private JobWorkerBuilderStep1 jobWorkerBuilderStep1;

  @Mock
  private JobWorkerBuilderStep1.JobWorkerBuilderStep2 jobWorkerBuilderStep2;

  @Mock
  private JobWorkerBuilderStep1.JobWorkerBuilderStep3 jobWorkerBuilderStep3;

  @Mock
  private JobWorker jobWorker;

  @DisplayName("Should build with a task handler supplied")
  @Test
  void build_with_task_handler() throws Exception {
    final String jobType = "job-type-1";
    final String jobName = "job-name-1";
    final Consumer<TaskReceivedEvent> taskHandler = event -> {
      MockJob mockJob = new MockJob();
      assertThat(event, is(notNullValue()));
      assertThat(event.getVariables(), equalTo(mockJob.getVariablesAsMap()));
      assertThat(event.getElementId(), equalTo(mockJob.getElementId()));
      assertThat(event.getProcessVersion(), equalTo(mockJob.getProcessDefinitionVersion() + ""));
      assertThat(event.getHeaders(), equalTo(mockJob.getCustomHeaders()));
      assertThat(event.getKey(), equalTo(mockJob.getKey() + ""));
      assertThat(event.getProcessId(), equalTo(mockJob.getBpmnProcessId() + ""));
    };
    when(applicationContext.getBean(ZeebeClient.class)).thenReturn(zeebeClient);
    when(zeebeClient.newWorker()).thenReturn(jobWorkerBuilderStep1);
    when(jobWorkerBuilderStep1.jobType(jobType)).thenReturn(jobWorkerBuilderStep2);
    when(jobWorkerBuilderStep2.handler(any())).thenReturn(jobWorkerBuilderStep3);
    when(jobWorkerBuilderStep3.name(jobName)).thenReturn(jobWorkerBuilderStep3);
    when(jobWorkerBuilderStep3.open()).thenReturn(jobWorker);
    DynamicJobWorkerRegistrar dynamicJobWorkerRegistrar = DynamicJobWorkerRegistrar.newInstance(applicationContext);
    final JobHandler mockJobHandler = dynamicJobWorkerRegistrar.mockJobHandler(new MockJob());
    dynamicJobWorkerRegistrar.withJobType(jobType);
    dynamicJobWorkerRegistrar.withJobName(jobName);
    dynamicJobWorkerRegistrar.withHandler(taskHandler);
    dynamicJobWorkerRegistrar.build();
    mockJobHandler.handle(null, new MockJob());
  }

  @DisplayName("Should build with spring event publisher supplied")
  @Test
  void build_with_spring_event_publisher() throws Exception {
    final String jobType = "job-type-1";
    final String jobName = "job-name-1";
    when(applicationContext.getBean(ZeebeClient.class)).thenReturn(zeebeClient);
    when(zeebeClient.newWorker()).thenReturn(jobWorkerBuilderStep1);
    when(jobWorkerBuilderStep1.jobType(jobType)).thenReturn(jobWorkerBuilderStep2);
    when(jobWorkerBuilderStep2.handler(any())).thenReturn(jobWorkerBuilderStep3);
    when(jobWorkerBuilderStep3.name(jobName)).thenReturn(jobWorkerBuilderStep3);
    when(jobWorkerBuilderStep3.open()).thenReturn(jobWorker);
    DynamicJobWorkerRegistrar dynamicJobWorkerRegistrar = DynamicJobWorkerRegistrar.newInstance(applicationContext);
    final MockJob mockJob = new MockJob();
    final JobHandler mockJobHandler = dynamicJobWorkerRegistrar.mockJobHandler(mockJob);
    dynamicJobWorkerRegistrar.withJobType(jobType);
    dynamicJobWorkerRegistrar.withJobName(jobName);
    dynamicJobWorkerRegistrar.emitSpringApplicationEvent(true);
    dynamicJobWorkerRegistrar.build();
    mockJobHandler.handle(null, mockJob);
    verify(applicationContext, times(1)).publishEvent(new TaskReceivedEvent(mockJob.getKey() + "",
      mockJob.getBpmnProcessId(),
      mockJob.getProcessDefinitionVersion() + "",
      mockJob.getElementId(),
      mockJob.getWorker(),
      mockJob.getVariablesAsMap(),
      mockJob.getCustomHeaders()));
  }

  @DisplayName("Should build with spring event publisher supplied")
  @Test
  void fail_when_both_spring_Event_and_task_handlers_are_supplied() {
    final String jobType = "job-type-1";
    final String jobName = "job-name-1";
    when(applicationContext.getBean(ZeebeClient.class)).thenReturn(zeebeClient);
    DynamicJobWorkerRegistrar dynamicJobWorkerRegistrar = DynamicJobWorkerRegistrar.newInstance(applicationContext);
    assertThrows(IllegalArgumentException.class,
      () -> {
        dynamicJobWorkerRegistrar.withJobType(jobType)
          .withHandler(t -> System.out.println(" My task handler "))
          .withJobName(jobName)
          .emitSpringApplicationEvent(true);
        dynamicJobWorkerRegistrar.build();
      });
  }
}
