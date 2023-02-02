package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.HashMap;
import java.util.Map;

public class MockJob implements ActivatedJob {

  @Override
  public long getKey() {
    return 0;
  }

  @Override
  public String getType() {
    return "Type";
  }

  @Override
  public long getProcessInstanceKey() {
    return 0;
  }

  @Override
  public String getBpmnProcessId() {
    return "BpmnProcessId";
  }

  @Override
  public int getProcessDefinitionVersion() {
    return 0;
  }

  @Override
  public long getProcessDefinitionKey() {
    return 0;
  }

  @Override
  public String getElementId() {
    return "ElementId";
  }

  @Override
  public long getElementInstanceKey() {
    return 0;
  }

  @Override
  public Map<String, String> getCustomHeaders() {
    return new HashMap<String, String>() {{
      put("header1", "value1");
    }};
  }

  @Override
  public String getWorker() {
    return "Worker";
  }

  @Override
  public int getRetries() {
    return 0;
  }

  @Override
  public long getDeadline() {
    return 0;
  }

  @Override
  public String getVariables() {
    return "";
  }

  @Override
  public Map<String, Object> getVariablesAsMap() {
    return new HashMap<String, Object>() {{
      put("variable1", "value1");
    }};
  }

  @Override
  public <T> T getVariablesAsType(Class<T> variableType) {
    return null;
  }

  @Override
  public String toJson() {
    return null;
  }
}
