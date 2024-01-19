package io.camunda.tasklist;

import io.camunda.common.http.HttpClient;
import io.camunda.tasklist.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamundaTaskListClient {

  private HttpClient httpClient;

  public static CamundaTaskListClientBuilder builder() {
    return new CamundaTaskListClientBuilder();
  }

  public Form getForm(String formId, String processDefinitionKey) {
    Map<String, String> paramsMap = new HashMap<>();
    paramsMap.put("processDefinitionKey", processDefinitionKey);
    return httpClient.get(Form.class, formId, paramsMap);
  }

  public Variable getVariable(String variableId) {
    return httpClient.get(Variable.class, variableId);
  }

  public void saveDraftVariables(String taskId, Variables variables) {
    httpClient.post(Void.class, String.class, null, variables);
  }

  public void searchVariables(String taskId, SearchVariables variables) {
    httpClient.post()
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }
}
