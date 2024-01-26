package io.camunda.operate.search;

import io.camunda.operate.model.DecisionInstanceInput;
import io.camunda.operate.model.DecisionInstanceOutput;
import io.camunda.operate.model.DecisionState;
import io.camunda.operate.model.DecisionType;
import java.util.List;

public class DecisionInstanceFilterBuilder {

  DecisionInstanceFilter filter;

  DecisionInstanceFilterBuilder() {
    filter = new DecisionInstanceFilter();
  }

  public DecisionInstanceFilterBuilder id(String id) {
    filter.setId(id);
    return this;
  }

  public DecisionInstanceFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public DecisionInstanceFilterBuilder state(DecisionState state) {
    filter.setState(state);
    return this;
  }

  public DecisionInstanceFilterBuilder evaluationDate(String evaluationDate) {
    filter.setEvaluationDate(evaluationDate);
    return this;
  }

  public DecisionInstanceFilterBuilder evaluationFailure(String evaluationFailure) {
    filter.setEvaluationFailure(evaluationFailure);
    return this;
  }

  public DecisionInstanceFilterBuilder processDefinitionKey(Long processDefinitionKey) {
    filter.setProcessDefinitionKey(processDefinitionKey);
    return this;
  }

  public DecisionInstanceFilterBuilder processInstanceKey(Long processInstanceKey) {
    filter.setProcessInstanceKey(processInstanceKey);
    return this;
  }

  public DecisionInstanceFilterBuilder decisionId(String decisionId) {
    filter.setDecisionId(decisionId);
    return this;
  }

  public DecisionInstanceFilterBuilder decisionDefinitionId(String decisionDefinitionId) {
    filter.setDecisionDefinitionId(decisionDefinitionId);
    return this;
  }

  public DecisionInstanceFilterBuilder decisionName(String decisionName) {
    filter.setDecisionName(decisionName);
    return this;
  }

  public DecisionInstanceFilterBuilder decisionVersion(Long decisionVersion) {
    filter.setDecisionVersion(decisionVersion);
    return this;
  }

  public DecisionInstanceFilterBuilder decisionType(DecisionType decisionType) {
    filter.setDecisionType(decisionType);
    return this;
  }

  public DecisionInstanceFilterBuilder result(String result) {
    filter.setResult(result);
    return this;
  }

  public DecisionInstanceFilterBuilder evaluatedInputs(
      List<DecisionInstanceInput> evaluatedInputs) {
    filter.setEvaluatedInputs(evaluatedInputs);
    return this;
  }

  public DecisionInstanceFilterBuilder evaluatedOutputs(
      List<DecisionInstanceOutput> evaluatedOutputs) {
    filter.setEvaluatedOutputs(evaluatedOutputs);
    return this;
  }

  public DecisionInstanceFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public DecisionInstanceFilter build() {
    return filter;
  }
}
