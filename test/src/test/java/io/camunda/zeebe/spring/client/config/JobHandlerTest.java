package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = {JobHandlerTest.class})
@ZeebeSpringAssertions
public class JobHandlerTest {

  @Autowired
  private ZeebeClient client;

  @Autowired
  private InMemoryEngine engine;

  private static boolean calledTest1 = false;

  @ZeebeWorker(name="test1", type = "test1", autoComplete = true)
  public void handleTest1(JobClient client, ActivatedJob job) {
    calledTest1 = true;
    System.out.println("############################### Job handled");
  }

  @Test
  public void testAutoComplete() {
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("test1")
      .startEvent()
      .serviceTask().zeebeJobType("test1")
      .endEvent()
      .done();

    System.out.println("############################### Deploy instance...");
    client.newDeployCommand().addProcessModel(bpmnModel, "test1.bpmn").send().join();

    final Map<String, Object> variables = Collections.singletonMap("magicNumber", "42"); // 42 instead of "42" fails?

    // when
    System.out.println("############################### Start instance...");
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test1", variables);

    // then
    assertThat(processInstance).isStarted();
    System.out.println("############################### start waiting...");
    waitForCompletion(processInstance);
    System.out.println("############################### ... finished waiting ");
    assertTrue(calledTest1);
  }

  @ZeebeWorker(type = "test2", autoComplete = true)
  public void handleTest2(JobClient client, ActivatedJob job) {
    // Complete it here to trigger a not found in the auto complete, which will be ignored
    client.newCompleteCommand(job.getKey()).send().join();
  }

  @Test
  public void testAutoCompleteOnAlreadyCompletedJob() {
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("test2").startEvent().serviceTask().zeebeJobType("test2").endEvent().done();
    client.newDeployCommand().addProcessModel(bpmnModel, "test2.bpmn").send().join();
    ProcessInstanceEvent processInstance = startProcessInstance(client, "test2");

    // then
    assertThat(processInstance).isStarted();
    System.out.println("############################### start waiting...");
    waitForCompletion(processInstance);
    System.out.println("############################### ... finished waiting ");
    assertTrue(calledTest1);
  }



  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId) {
    return startProcessInstance(client, bpmnProcessId, new HashMap<>());
  }

  private ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, Map<String, Object> variables) {
    return client.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion().variables(variables).send().join();
  }

  // TODO find a better solution for this
  public void waitForCompletion(ProcessInstanceEvent processInstance) {
    Awaitility.await().atMost(Duration.ofMillis(2000)).untilAsserted(() -> {
      Thread.sleep(1000L);
      RecordStreamSourceStore.init(engine.getRecordStream());
      assertThat(processInstance).isCompleted();
      Thread.sleep(500L);
    });
  }

}
