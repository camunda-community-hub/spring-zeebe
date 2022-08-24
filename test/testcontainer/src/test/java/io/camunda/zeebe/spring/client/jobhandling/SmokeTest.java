package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.properties.ZeebeClientProperties;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = {SmokeTest.class})
@ZeebeSpringTest
public class SmokeTest {

  @Autowired
  private ZeebeClient client;

  @Autowired
  private ZeebeTestEngine engine;

  @MockBean
  private ZeebeClientProperties zeebeClientProperties;

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

    client.newDeployResourceCommand().addProcessModel(bpmnModel, "test1.bpmn").send().join();

    final Map<String, Object> variables = Collections.singletonMap("magicNumber", "42"); // Todo: 42 instead of "42" fails?
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test1", variables);

    assertThat(processInstance).isStarted();
    waitForProcessInstanceCompleted(processInstance);
    assertTrue(calledTest1);
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId) {
    return startProcessInstance(client, bpmnProcessId, new HashMap<>());
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, Map<String, Object> variables) {
    return client.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion().variables(variables).send().join();
  }

}
