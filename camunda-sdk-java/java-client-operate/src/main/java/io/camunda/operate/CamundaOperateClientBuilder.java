package io.camunda.operate;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.http.DefaultHttpClient;
import io.camunda.common.http.HttpClient;
import io.camunda.operate.model.*;

import java.util.HashMap;
import java.util.Map;

public class CamundaOperateClientBuilder {

  private CamundaOperateClient client;
  private Authentication authentication;
  private String operateUrl;
  private HttpClient httpClient;


  public CamundaOperateClientBuilder authentication(Authentication authentication) {
    this.authentication = authentication;
    return this;
  }

  public CamundaOperateClientBuilder operateUrl(String operateUrl) {
    this.operateUrl = formatUrl(operateUrl);
    return this;
  }

  public CamundaOperateClientBuilder setup() {
    httpClient = new DefaultHttpClient(authentication);
    httpClient.init(operateUrl, "/v1");
    // load the config map
    Map<Class<?>, String> map = new HashMap<>();
    map.put(ProcessInstance.class, "/process-instances");
    map.put(ListTypeToken.listFlowNodeStatistics.getClass(), "/process-instances/{key}/statistics");
    map.put(ListTypeToken.listSequenceFlows.getClass(), "/process-instances/{key}/sequence-flows");
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

  private String formatUrl(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length()-1);
    }
    return url;
  }

  public CamundaOperateClient build() {
    client = new CamundaOperateClient();
    client.setHttpClient(httpClient);
    return client;
  }
}
