package io.camunda.operate.model;

import java.util.List;

public class DecisionInstance {

  private String id;
  private Long key;
  private DecisionState state;
  private String evaluationDate;
  private String evaluationFailure;
  private Long processDefinitionKey;
  private Long processInstanceKey;
  private String decisionId;
  private String decisionDefinitionId;
  private String decisionName;
  private Long decisionVersion;
  private DecisionType decisionType;
  private String result;
  private List<DecisionInstanceInput> evaluatedInputs;
  private List<DecisionInstanceOutput> evaluatedOutputs;
  private String tenantId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public DecisionState getState() {
    return state;
  }

  public void setState(DecisionState state) {
    this.state = state;
  }

  public String getEvaluationDate() {
    return evaluationDate;
  }

  public void setEvaluationDate(String evaluationDate) {
    this.evaluationDate = evaluationDate;
  }

  public String getEvaluationFailure() {
    return evaluationFailure;
  }

  public void setEvaluationFailure(String evaluationFailure) {
    this.evaluationFailure = evaluationFailure;
  }

  public Long getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(Long processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public Long getProcessInstanceKey() {
    return processInstanceKey;
  }

  public void setProcessInstanceKey(Long processInstanceKey) {
    this.processInstanceKey = processInstanceKey;
  }

  public String getDecisionId() {
    return decisionId;
  }

  public void setDecisionId(String decisionId) {
    this.decisionId = decisionId;
  }

  public String getDecisionDefinitionId() {
    return decisionDefinitionId;
  }

  public void setDecisionDefinitionId(String decisionDefinitionId) {
    this.decisionDefinitionId = decisionDefinitionId;
  }

  public String getDecisionName() {
    return decisionName;
  }

  public void setDecisionName(String decisionName) {
    this.decisionName = decisionName;
  }

  public Long getDecisionVersion() {
    return decisionVersion;
  }

  public void setDecisionVersion(Long decisionVersion) {
    this.decisionVersion = decisionVersion;
  }

  public DecisionType getDecisionType() {
    return decisionType;
  }

  public void setDecisionType(DecisionType decisionType) {
    this.decisionType = decisionType;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public List<DecisionInstanceInput> getEvaluatedInputs() {
    return evaluatedInputs;
  }

  public void setEvaluatedInputs(List<DecisionInstanceInput> evaluatedInputs) {
    this.evaluatedInputs = evaluatedInputs;
  }

  public List<DecisionInstanceOutput> getEvaluatedOutputs() {
    return evaluatedOutputs;
  }

  public void setEvaluatedOutputs(List<DecisionInstanceOutput> evaluatedOutputs) {
    this.evaluatedOutputs = evaluatedOutputs;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
