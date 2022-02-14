package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {JobHandlerTest.class})
@ZeebeSpringTest
public class JobHandlerTest {

  @Autowired
  private ZeebeClient client;

  @Autowired
  private InMemoryEngine engine;

  private static boolean calledTest1 = false;
  private static boolean calledTest2 = false;

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

    client.newDeployCommand().addProcessModel(bpmnModel, "test1.bpmn").send().join();

    final Map<String, Object> variables = Collections.singletonMap("magicNumber", "42"); // Todo: 42 instead of "42" fails?
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test1", variables);

    assertThat(processInstance).isStarted();
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
    client.newDeployCommand().addProcessModel(bpmnModel, "test2.bpmn").send().join();
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test2");
    //assertThat(processInstance).isStarted();
    waitForProcessInstanceCompleted(processInstance);
    assertTrue(calledTest2);
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId) {
    return startProcessInstance(client, bpmnProcessId, new HashMap<>());
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, Map<String, Object> variables) {
    return client.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion().variables(variables).send().join();
  }

}
