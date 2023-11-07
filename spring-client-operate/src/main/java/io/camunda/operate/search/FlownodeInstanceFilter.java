package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.model.FlowNodeInstanceState;
import io.camunda.operate.exception.OperateException;

@JsonInclude(Include.NON_NULL)
public class FlownodeInstanceFilter implements Filter {
  private Long processInstanceKey;
  private Long processDefinitionKey;
  private Long incidentKey;
  private String type;
  private String flowNodeId;
  private DateFilter startDate;
  private DateFilter endDate;
  private FlowNodeInstanceState state;
  private Boolean incident;
  private String flowNodeName;

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

  public DateFilter getStartDate() {
    return startDate;
  }

  public void setStartDate(DateFilter startDate) {
    this.startDate = startDate;
  }

  public DateFilter getEndDate() {
    return endDate;
  }

  public void setEndDate(DateFilter endDate) {
    this.endDate = endDate;
  }

  public String getFlowNodeId() {
    return flowNodeId;
  }

  public void setFlowNodeId(String flowNodeId) {
    this.flowNodeId = flowNodeId;
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

  public String getFlowNodeName() {
    return flowNodeName;
  }

  public void setFlowNodeName(String flowNodeName) {
    this.flowNodeName = flowNodeName;
  }

  public static class Builder {
    private Long processInstanceKey;
    private Long processDefinitionKey;
    private Long incidentKey;
    private String type;
    private DateFilter startDate;
    private DateFilter endDate;
    private String flowNodeId;
    private FlowNodeInstanceState state;
    private Boolean incident;
    private String flowNodeName;

    public Builder() {
      super();
    }

    public Builder processInstanceKey(Long processInstanceKey) {
      this.processInstanceKey = processInstanceKey;
      return this;
    }

    public Builder processDefinitionKey(Long processDefinitionKey) {
      this.processDefinitionKey = processDefinitionKey;
      return this;
    }

    public Builder incidentKey(Long incidentKey) {
      this.incidentKey = incidentKey;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder startDate(DateFilter startDate) {
      this.startDate = startDate;
      return this;
    }

    public Builder endDate(DateFilter endDate) {
      this.endDate = endDate;
      return this;
    }

    public Builder flowNodeId(String flowNodeId) {
      this.flowNodeId = flowNodeId;
      return this;
    }

    public Builder state(FlowNodeInstanceState state) {
      this.state = state;
      return this;
    }

    public Builder incident(Boolean incident) {
      this.incident = incident;
      return this;
    }

    public Builder flowNodeName(String flowNodeName) {
      this.flowNodeName = flowNodeName;
      return this;
    }

    public FlownodeInstanceFilter build() throws OperateException {
      FlownodeInstanceFilter processInstanceFilter = new FlownodeInstanceFilter();
      processInstanceFilter.processInstanceKey = processInstanceKey;
      processInstanceFilter.processDefinitionKey = processDefinitionKey;
      processInstanceFilter.incidentKey = incidentKey;
      processInstanceFilter.type = type;
      processInstanceFilter.startDate = startDate;
      processInstanceFilter.endDate = endDate;
      processInstanceFilter.flowNodeId = flowNodeId;
      processInstanceFilter.state = state;
      processInstanceFilter.incident = incident;
      processInstanceFilter.flowNodeName = flowNodeName;
      return processInstanceFilter;
    }
  }
}
