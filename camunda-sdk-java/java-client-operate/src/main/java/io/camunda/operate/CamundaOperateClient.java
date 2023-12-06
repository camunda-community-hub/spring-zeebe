package io.camunda.operate;

import io.camunda.common.http.HttpClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.*;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class CamundaOperateClient {

  private HttpClient httpClient;

  public static CamundaOperateClientBuilder builder() {
    return new CamundaOperateClientBuilder();
  }

  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return httpClient.get(ProcessDefinition.class, key);
  }

  public BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException {
    String xml = getProcessDefinitionXml(key);
    try {
        InputStream processInputStream = new ByteArrayInputStream(
          xml.getBytes());
        return Bpmn.readModelFromStream(processInputStream);
    } catch (Exception e) {
      throw new OperateException(e);
    }
  }

  public String getProcessDefinitionXml(Long key) throws OperateException {
    return httpClient.getXml(ProcessDefinition.class, key);
  }

  public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query) throws OperateException {
    return searchProcessDefinitionResults(query).getItems();
  }

  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, ProcessDefinition.class, SearchResultTypeToken.searchResultProcessDefinition, query);
  }

  public List<DecisionDefinition> searchDecisionDefinitions(SearchQuery query) throws OperateException {
    return searchDecisionDefinitionResults(query).getItems();
  }

  public SearchResult<DecisionDefinition> searchDecisionDefinitionResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, DecisionDefinition.class, SearchResultTypeToken.searchResultDecisionDefinition, query);
  }

  public List<DecisionInstance> searchDecisionInstances(SearchQuery query) throws OperateException {
    return searchDecisionInstanceResults(query).getItems();
  }

  public SearchResult<DecisionInstance> searchDecisionInstanceResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, DecisionInstance.class, SearchResultTypeToken.searchResultDecisionInstance, query);
  }

  public List<FlowNodeInstance> searchFlowNodeInstances(SearchQuery query) throws OperateException {
    return searchFlowNodeInstanceResults(query).getItems();
  }

  public SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, FlowNodeInstance.class, SearchResultTypeToken.searchResultFlowNodeInstance, query);
  }

  public List<Variable> searchVariables(SearchQuery query) throws OperateException {
    return searchVariableResults(query).getItems();
  }

  public SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, Variable.class, SearchResultTypeToken.searchResultVariable, query);
  }

  public List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
    return searchProcessInstanceResults(query).getItems();
  }

  public SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, ProcessInstance.class, SearchResultTypeToken.searchResultProcessInstance, query);
  }

  public List<DecisionRequirements> searchDecisionRequirements(SearchQuery query) throws OperateException {
    return searchDecisionRequirementsResults(query).getItems();
  }

  public SearchResult<DecisionRequirements> searchDecisionRequirementsResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, DecisionRequirements.class, SearchResultTypeToken.searchResultDecisionRequirements, query);
  }

  public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    return searchIncidentResults(query).getItems();
  }

  public SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, Incident.class, SearchResultTypeToken.searchResultIncident, query);
  }

  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return httpClient.get(ProcessInstance.class, key);
  }

  public ChangeStatus deleteProcessInstance(Long key) throws OperateException {
    return httpClient.delete(ChangeStatus.class, ProcessInstance.class, key);
  }

  public List<FlowNodeStatistics> getFlowNodeStatistics(Long key) throws OperateException {
    return httpClient.get(List.class, FlowNodeStatistics.class, ListTypeToken.listFlowNodeStatistics, key);
  }

  public List<String> getSequenceFlows(Long key) throws OperateException {
    return httpClient.get(List.class, String.class, ListTypeToken.listSequenceFlows, key);
  }

  public FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException {
    return httpClient.get(FlowNodeInstance.class, key);
  }

  public Incident getIncident(Long key) throws OperateException {
    return httpClient.get(Incident.class, key);
  }

  public DecisionDefinition getDecisionDefinition(Long key) throws OperateException {
    return httpClient.get(DecisionDefinition.class, key);
  }

  public DecisionRequirements getDecisionRequirements(Long key) throws OperateException {
    return httpClient.get(DecisionRequirements.class, key);
  }

  public String getDecisionRequirementsXml(Long key) throws OperateException {
    return httpClient.getXml(DecisionRequirements.class, key);
  }

  public DecisionInstance getDecisionInstance(String id) throws OperateException {
    return httpClient.get(DecisionInstance.class, id);
  }

  public Variable getVariable(Long key) throws OperateException {
    return httpClient.get(Variable.class, key);
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }
}
