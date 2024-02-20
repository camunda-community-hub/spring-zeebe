package io.camunda.operate.model;

import com.google.common.reflect.TypeToken;

// See https://github.com/google/guava/wiki/ReflectionExplained
public class SearchResultTypeToken {

  public static TypeToken<SearchResult<ProcessDefinition>> searchResultProcessDefinition =
      new TypeToken<SearchResult<ProcessDefinition>>() {};
  public static TypeToken<SearchResult<DecisionDefinition>> searchResultDecisionDefinition =
      new TypeToken<SearchResult<DecisionDefinition>>() {};
  public static TypeToken<SearchResult<DecisionInstance>> searchResultDecisionInstance =
      new TypeToken<SearchResult<DecisionInstance>>() {};
  public static TypeToken<SearchResult<FlowNodeInstance>> searchResultFlowNodeInstance =
      new TypeToken<SearchResult<FlowNodeInstance>>() {};
  public static TypeToken<SearchResult<Variable>> searchResultVariable =
      new TypeToken<SearchResult<Variable>>() {};
  public static TypeToken<SearchResult<ProcessInstance>> searchResultProcessInstance =
      new TypeToken<SearchResult<ProcessInstance>>() {};
  public static TypeToken<SearchResult<DecisionRequirements>> searchResultDecisionRequirements =
      new TypeToken<SearchResult<DecisionRequirements>>() {};
  public static TypeToken<SearchResult<Incident>> searchResultIncident =
      new TypeToken<SearchResult<Incident>>() {};
}
