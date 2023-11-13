package io.camunda.operate;

import io.camunda.commons.auth.Authentication;
import io.camunda.commons.auth.Product;
import io.camunda.commons.http.*;
import io.camunda.operate.model.*;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.SearchQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamundaOperateClient {

  private HttpClient httpClient;

  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return httpClient.get(ProcessDefinition.class, key);
  }

  public String getProcessDefinitionXml(Long key) throws OperateException {
    return httpClient.getXml(ProcessDefinition.class, key);
  }

  // TODO: implement the search APIs

  public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query) throws OperateException {
    return searchProcessDefinitionResults(query).getItems();
  }

  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query) throws OperateException {
    return httpClient.post(SearchResult.class, ProcessDefinition.class, SearchResultTypeToken.searchResultProcessDefinition, query);
  }

  public List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  // TODO: implement XML APIs

  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return httpClient.get(ProcessInstance.class, key);
  }

  public FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException {
    return httpClient.get(FlowNodeInstance.class, key);
  }

  public List<FlowNodeInstance> searchFlowNodeInstances(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
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

  public DecisionInstance getDecisionInstance(String id) throws OperateException {
    return httpClient.get(DecisionInstance.class, id);
  }

  public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public Variable getVariable(Long key) throws OperateException {
    return httpClient.get(Variable.class, key);
  }

  public List<Variable> searchVariables(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public static class Builder {

    private Authentication authentication;

    private String operateUrl;

    private HttpClient httpClient;

    // TODO: Do we still need to consider beta?
    private boolean beta;

    public Builder() {

    }

    public Builder beta() {
      beta = true;
      return this;
    }

    public Builder authentication(Authentication authentication) {
      this.authentication = authentication;
      return this;
    }

    public Builder operateUrl(String operateUrl) {
      this.operateUrl = formatUrl(operateUrl);
      return this;
    }

    public CamundaOperateClient build() throws OperateException {
      CamundaOperateClient client = new CamundaOperateClient();
      client.httpClient = httpClient;
      return client;
    }

    private String formatUrl(String url) {
      if (url.endsWith("/")) {
        return url.substring(0, url.length()-1);
      }
      return url;
    }

    public Builder setup() {
      httpClient = new DefaultHttpClient(authentication);
      httpClient.init(operateUrl, "/v1");
      // load the config map
      Map<Class<?>, String> map = new HashMap<>();
      map.put(ProcessInstance.class, "/process-instances");
      map.put(ProcessDefinition.class, "/process-definitions");
      map.put(FlowNodeInstance.class, "/flownode-instances");
      map.put(Incident.class, "/incidents");
      map.put(Variable.class, "/variables");
      map.put(DecisionDefinition.class, "/decision-definitions");
      map.put(DecisionRequirements.class, "/drd");
      map.put(DecisionInstance.class, "/decision-instances");
      map.put(SearchResultTypeToken.searchResultProcessDefinition.getClass(), "/process-definitions/search");
      map.put(SearchResultTypeToken.searchResultDecisionDefinition.getClass(), "/decision-definitions/search");
      map.put(SearchResultTypeToken.searchResultDecisionInstance.getClass(), "/decision-instances/search");
      map.put(SearchResultTypeToken.searchResultFlowNodeInstance.getClass(), "/flownode-instances/search");
      map.put(SearchResultTypeToken.searchResultVariable.getClass(), "/variables/search");
      map.put(SearchResultTypeToken.searchResultProcessInstance.getClass(), "/process-instances/search");
      map.put(SearchResultTypeToken.searchResultDecisionRequirements.getClass(), "/drd/search");
      map.put(SearchResultTypeToken.searchResultIncident.getClass(), "/incidents/search");
      httpClient.loadMap(Product.OPERATE, map);
      return this;
    }
  }
}
