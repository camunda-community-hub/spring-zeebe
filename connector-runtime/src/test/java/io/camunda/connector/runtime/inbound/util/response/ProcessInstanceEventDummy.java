package io.camunda.connector.runtime.inbound.util.response;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

public class ProcessInstanceEventDummy implements ProcessInstanceEvent {
  public long getProcessDefinitionKey() {
    return 0;
  }

  public String getBpmnProcessId() {
    return null;
  }

  public int getVersion() {
    return 0;
  }

  public long getProcessInstanceKey() {
    return 0;
  }
}
