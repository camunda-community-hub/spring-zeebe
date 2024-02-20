package io.camunda.operate.search;

import io.camunda.operate.model.FlowNodeInstanceState;
import java.util.Date;

public class FlowNodeInstanceFilterBuilder {

  FlowNodeInstanceFilter filter;

  FlowNodeInstanceFilterBuilder() {
    filter = new FlowNodeInstanceFilter();
  }

  public FlowNodeInstanceFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public FlowNodeInstanceFilterBuilder processInstanceKey(Long processInstanceKey) {
    filter.setProcessInstanceKey(processInstanceKey);
    return this;
  }

  public FlowNodeInstanceFilterBuilder processDefinitionKey(Long processDefinitionKey) {
    filter.setProcessDefinitionKey(processDefinitionKey);
    return this;
  }

  public FlowNodeInstanceFilterBuilder startDate(Date startDate) {
    filter.setStartDate(startDate);
    return this;
  }

  public FlowNodeInstanceFilterBuilder endDate(Date endDate) {
    filter.setEndDate(endDate);
    return this;
  }

  public FlowNodeInstanceFilterBuilder flowNodeId(String flowNodeId) {
    filter.setFlowNodeId(flowNodeId);
    return this;
  }

  public FlowNodeInstanceFilterBuilder flowNodeName(String flowNodeName) {
    filter.setFlowNodeName(flowNodeName);
    return this;
  }

  public FlowNodeInstanceFilterBuilder incidentKey(Long incidentKey) {
    filter.setIncidentKey(incidentKey);
    return this;
  }

  public FlowNodeInstanceFilterBuilder type(String type) {
    filter.setType(type);
    return this;
  }

  public FlowNodeInstanceFilterBuilder state(FlowNodeInstanceState state) {
    filter.setState(state);
    return this;
  }

  public FlowNodeInstanceFilterBuilder incident(Boolean incident) {
    filter.setIncident(incident);
    return this;
  }

  public FlowNodeInstanceFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public FlowNodeInstanceFilter build() {
    return filter;
  }
}
