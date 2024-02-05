package io.camunda.operate.model;

import java.util.Date;

public class FlowNodeInstance {
  private Long key;
  private Long processInstanceKey;
  private Long processDefinitionKey;
  private Date startDate;
  private Date endDate;
  private String flowNodeId;
  private String flowNodeName;
  private Long incidentKey;
  private String type;
  private FlowNodeInstanceState state;
  private Boolean incident;
  private String tenantId;

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public Long getProcessInstanceKey() {
    return processInstanceKey;
  }

  public void setProcessInstanceKey(Long processInstanceKey) {
    this.processInstanceKey = processInstanceKey;
  }

  public Long getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(Long processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getFlowNodeId() {
    return flowNodeId;
  }

  public void setFlowNodeId(String flowNodeId) {
    this.flowNodeId = flowNodeId;
  }

  public String getFlowNodeName() {
    return flowNodeName;
  }

  public void setFlowNodeName(String flowNodeName) {
    this.flowNodeName = flowNodeName;
  }

  public Long getIncidentKey() {
    return incidentKey;
  }

  public void setIncidentKey(Long incidentKey) {
    this.incidentKey = incidentKey;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public FlowNodeInstanceState getState() {
    return state;
  }

  public void setState(FlowNodeInstanceState state) {
    this.state = state;
  }

  public Boolean getIncident() {
    return incident;
  }

  public void setIncident(Boolean incident) {
    this.incident = incident;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
