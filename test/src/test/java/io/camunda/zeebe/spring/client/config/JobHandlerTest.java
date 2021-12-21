package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = {JobHandlerTest.class})
@ZeebeSpringAssertions
public class JobHandlerTest {

  @Autowired
  private ZeebeClient client;

  private static boolean calledTest1 = false;

  @Test
  public void testProcessInstanceIsStarted() {
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("test1")
      .startEvent()
      .serviceTask().zeebeJobType("test1")
      .endEvent()
      .done();

    client.newDeployCommand().addProcessModel(bpmnModel, "test1.bpmn").send().join();

    final Map<String, Object> variables = Collections.singletonMap("magicNumber", "42"); // 42 instead of "42" fails?

    // when
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test1", variables);

    // then
    assertThat(processInstance).isStarted();
    waitForIdleState();
    assertThat(processInstance).isCompleted();
    assertTrue(calledTest1);
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, Map<String, Object> variables) {
    return client.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion().variables(variables).send().join();
  }

  private void waitForIdleState() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @ZeebeWorker(type = "test1", autoComplete = true)
  public void handleTest1(JobClient client, ActivatedJob job) {
    // Complete it here to trigger a not found in the auto complete!
    //client.newCompleteCommand(job.getKey()).send().join();
    calledTest1 = true;
  }
}
