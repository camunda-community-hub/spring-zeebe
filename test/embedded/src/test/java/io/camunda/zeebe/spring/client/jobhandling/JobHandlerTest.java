package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.builder.ServiceTaskBuilder;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {JobHandlerTest.class, JobHandlerTest.ZeebeCustomizerDisableWorkerConfiguration.class})
@ZeebeSpringTest
public class JobHandlerTest {

  @Autowired
  private ZeebeClient client;

  @TestConfiguration
  public static class ZeebeCustomizerDisableWorkerConfiguration {

    @Bean
    public ZeebeWorkerValueCustomizer zeebeWorkerValueCustomizer() {
      return zeebeWorker -> {
        if(zeebeWorker.getType().equals("test4")){
          zeebeWorker.setEnabled(false);
        }
      };
    }
  }


  private static boolean calledTest1 = false;
  private static boolean calledTest2 = false;
  private static boolean calledTest3 = false;
  private static boolean calledTest4 = false;

  @ZeebeWorker(name="test1", type = "test1", autoComplete = true)
  public void handleTest1(JobClient client, ActivatedJob job) {
    calledTest1 = true;
  }

  @Test
  public void testAutoComplete() {
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("test1")
      .startEvent()
      .serviceTask().zeebeJobType("test1")
      .endEvent()
      .done();

    client.newDeployResourceCommand().addProcessModel(bpmnModel, "test1.bpmn").send().join();

    final Map<String, Object> variables = Collections.singletonMap("magicNumber", "42"); // Todo: 42 instead of "42" fails?
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test1", variables);

    waitForProcessInstanceCompleted(processInstance);
    assertTrue(calledTest1);
  }

  @ZeebeWorker(type = "test2", autoComplete = true)
  public void handleTest2(JobClient client, ActivatedJob job) {
    // Complete it here to trigger a not found in the auto complete, which will be ignored
    client.newCompleteCommand(job.getKey()).send().join();
    calledTest2 = true;
  }

  @Test
  public void testAutoCompleteOnAlreadyCompletedJob() {
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("test2").startEvent().serviceTask().zeebeJobType("test2").endEvent().done();
    client.newDeployResourceCommand().addProcessModel(bpmnModel, "test2.bpmn").send().join();
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test2");
    //assertThat(processInstance).isStarted();
    waitForProcessInstanceCompleted(processInstance);
    assertTrue(calledTest2);
  }

  @ZeebeWorker(name = "test3", type = "test3", autoComplete = true, pollInterval = 10, enabled = false)
  public void handeTest3Disabled(final JobClient client, final ActivatedJob job) {
    calledTest3 = true;
  }

  @Test
  void shouldNotActivateJobInAnnotationDisabledWorker() {
    final String processId = "test3";
    final ServiceTaskBuilder serviceTaskBuilder = Bpmn.createExecutableProcess(processId)
      .startEvent()
      .serviceTask().zeebeJobType(processId);
    // At the first we are creating the timer boundary event - if we aren't activated for 100 ms - we end the test.
    serviceTaskBuilder.boundaryEvent().timerWithDuration(Duration.ofMillis(100).toString()).endEvent();
    // But if we broke something and the job is successfully activated - we are throwing the "shouldNotPass" error thus the process instance will never be completed positively if we are going next on this branch.
    final BpmnModelInstance bpmnModelInstance = serviceTaskBuilder.endEvent().error("shouldNotPass").done();
    client.newDeployResourceCommand().addProcessModel(bpmnModelInstance, "test3.bpmn").send().join();
    final ProcessInstanceEvent processInstance = startProcessInstance(client, processId);
    waitForProcessInstanceCompleted(processInstance);
    // The double-check that we didn't go to the worker.
    assertThat(calledTest3).isFalse();
  }

  @ZeebeWorker(name = "test4", type = "test4", autoComplete = true, pollInterval = 10)
  public void handeTest4(final JobClient client, final ActivatedJob job) {
    calledTest4 = true;
  }

  /**
   * Worker disabled in {@link ZeebeCustomizerDisableWorkerConfiguration#zeebeWorkerValueCustomizer()}
   */
  @Test
  void shouldNotActivateJobInPropertiesDisabledWorker() {
    final String processId = "test4";
    final ServiceTaskBuilder serviceTaskBuilder = Bpmn.createExecutableProcess(processId)
      .startEvent()
      .serviceTask().zeebeJobType(processId);
    // At the first we are creating the timer boundary event - if we aren't activated for 100 ms - we end the test.
    serviceTaskBuilder.boundaryEvent().timerWithDuration(Duration.ofMillis(100).toString()).endEvent();
    // But if we broke something and the job is successfully activated - we are throwing the "shouldNotPass" error thus the process instance will never be completed positively if we are going next on this branch.
    final BpmnModelInstance bpmnModelInstance = serviceTaskBuilder.endEvent().error("shouldNotPass").done();
    client.newDeployResourceCommand().addProcessModel(bpmnModelInstance, "test4.bpmn").send().join();
    final ProcessInstanceEvent processInstance = startProcessInstance(client, processId);
    waitForProcessInstanceCompleted(processInstance);
    // The double-check that we didn't go to the worker.
    assertThat(calledTest4).isFalse();
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId) {
    return startProcessInstance(client, bpmnProcessId, new HashMap<>());
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, Map<String, Object> variables) {
    return client.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion().variables(variables).send().join();
  }

}
