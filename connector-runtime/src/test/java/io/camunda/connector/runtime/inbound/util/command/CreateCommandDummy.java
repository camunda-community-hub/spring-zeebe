package io.camunda.connector.runtime.inbound.util.command;

import io.camunda.connector.runtime.inbound.util.response.ProcessInstanceEventDummy;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.impl.ZeebeClientFutureImpl;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

public class CreateCommandDummy
  implements CreateProcessInstanceCommandStep1,
  CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2,
  CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3 {

  public CreateProcessInstanceCommandStep2 bpmnProcessId(String bpmnProcessId) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 processDefinitionKey(long processDefinitionKey) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 version(int version) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 latestVersion() {
    return this;
  }

  public CreateProcessInstanceCommandStep3 variables(InputStream variables) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 variables(String variables) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 variables(Map<String, Object> variables) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 variables(Object variables) {
    return this;
  }

  public CreateProcessInstanceCommandStep3 startBeforeElement(String elementId) {
    return this;
  }

  public CreateProcessInstanceWithResultCommandStep1 withResult() {
    return null;
  }

  public FinalCommandStep<ProcessInstanceEvent> requestTimeout(Duration requestTimeout) {
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public ZeebeFuture<ProcessInstanceEvent> send() {
    ZeebeClientFutureImpl future = new ZeebeClientFutureImpl<>();
    future.complete(new ProcessInstanceEventDummy());
    return future;
  }
}
