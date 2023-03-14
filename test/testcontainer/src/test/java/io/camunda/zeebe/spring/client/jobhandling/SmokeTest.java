package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {SmokeTest.class},
  properties = { "zeebe.client.worker.default-type=DefaultType" })
@ZeebeSpringTest
public class SmokeTest {

  @Autowired
  private ZeebeClient client;

  @Autowired
  private ZeebeTestEngine engine;

  private static boolean calledTest1 = false;

  private static boolean calledTest2 = false;
  private static ComplexTypeDTO test2ComplexTypeDTO = null;
  private static String test2Var2 = null;

  @JobWorker(name="test1", type = "test1") // autoComplete is true
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

  @JobWorker(name = "test2", type = "test2", pollInterval = 10)
  public void handleTest2(final JobClient client, final ActivatedJob job, @Variable ComplexTypeDTO dto, @Variable String var2) {
    calledTest2 = true;
    test2ComplexTypeDTO = dto;
    test2Var2 = var2;
  }

  @Test
  void testShouldDeserializeComplexTypeZebeeVariable() {
    final String processId = "test2";
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess(processId).startEvent().serviceTask().zeebeJobType(processId).endEvent().done();
    client.newDeployResourceCommand().addProcessModel(bpmnModel, processId + ".bpmn").send().join();

    ComplexTypeDTO dto = new ComplexTypeDTO();
    dto.setVar1("value1");
    dto.setVar2("value2");

    Map<String, Object> variables = new HashMap<>();
    variables.put("dto", dto);
    variables.put("var2", "stringValue");

    ProcessInstanceEvent processInstance = startProcessInstance(client, processId, variables);
    waitForProcessInstanceCompleted(processInstance);

    Assertions.assertTrue(calledTest2);
    assertNotNull(test2ComplexTypeDTO);
    assertNotEquals(new ComplexTypeDTO(), test2ComplexTypeDTO);
    assertEquals("value1", test2ComplexTypeDTO.getVar1());
    assertEquals("value2", test2ComplexTypeDTO.getVar2());
    assertNotNull(test2Var2);
    assertEquals("stringValue", test2Var2);
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId) {
    return startProcessInstance(client, bpmnProcessId, new HashMap<>());
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, Map<String, Object> variables) {
    return client.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion().variables(variables).send().join();
  }

  private static class ComplexTypeDTO {
    private String var1;
    private String var2;

    public String getVar1() {
      return var1;
    }

    public void setVar1(String var1) {
      this.var1 = var1;
    }

    public String getVar2() {
      return var2;
    }

    public void setVar2(String var2) {
      this.var2 = var2;
    }
  }

}
