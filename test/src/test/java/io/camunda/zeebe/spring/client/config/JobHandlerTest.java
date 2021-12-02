package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.config.ZeebeSpringAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Collections;
import java.util.Map;

import static io.camunda.zeebe.bpmnassert.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@SpringBootConfiguration
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
  public void handleTest1() {
    calledTest1 = true;
  }
}
