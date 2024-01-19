package io.camunda.tasklist.model;

import java.util.List;

public class TaskSearch {

  private String state;
  private Boolean assigned;
  private String assignee;
  private String taskDefinitionId;
  private String candidateGroup;
  private String candidateUser;
  private String processDefinitionKey;
  private String processInstanceKey;
  private Integer pageSize;
  private DateFilter followUpDate;
  private DateFilter dueDate;
  private List<TaskByVariables> taskVariables;
  private List<String> tenantIds;
  private List<TaskOrderBy> sort;
  private List<String> searchAfter;
  private List<String> searchAfterOrEqual;
  private List<String> searchBefore;
  private List<String> searchBeforeOrEqual;

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Boolean getAssigned() {
    return assigned;
  }

  public void setAssigned(Boolean assigned) {
    this.assigned = assigned;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getTaskDefinitionId() {
    return taskDefinitionId;
  }

  public void setTaskDefinitionId(String taskDefinitionId) {
    this.taskDefinitionId = taskDefinitionId;
  }

  public String getCandidateGroup() {
    return candidateGroup;
  }

  public void setCandidateGroup(String candidateGroup) {
    this.candidateGroup = candidateGroup;
  }

  public String getCandidateUser() {
    return candidateUser;
  }

  public void setCandidateUser(String candidateUser) {
    this.candidateUser = candidateUser;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public String getProcessInstanceKey() {
    return processInstanceKey;
  }

  public void setProcessInstanceKey(String processInstanceKey) {
    this.processInstanceKey = processInstanceKey;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public DateFilter getFollowUpDate() {
    return followUpDate;
  }

  public void setFollowUpDate(DateFilter followUpDate) {
    this.followUpDate = followUpDate;
  }

  public DateFilter getDueDate() {
    return dueDate;
  }

  public void setDueDate(DateFilter dueDate) {
    this.dueDate = dueDate;
  }

  public List<TaskByVariables> getTaskVariables() {
    return taskVariables;
  }

  public void setTaskVariables(List<TaskByVariables> taskVariables) {
    this.taskVariables = taskVariables;
  }

  public List<String> getTenantIds() {
    return tenantIds;
  }

  public void setTenantIds(List<String> tenantIds) {
    this.tenantIds = tenantIds;
  }

  public List<TaskOrderBy> getSort() {
    return sort;
  }

  public void setSort(List<TaskOrderBy> sort) {
    this.sort = sort;
  }

  public List<String> getSearchAfter() {
    return searchAfter;
  }

  public void setSearchAfter(List<String> searchAfter) {
    this.searchAfter = searchAfter;
  }

  public List<String> getSearchAfterOrEqual() {
    return searchAfterOrEqual;
  }

  public void setSearchAfterOrEqual(List<String> searchAfterOrEqual) {
    this.searchAfterOrEqual = searchAfterOrEqual;
  }

  public List<String> getSearchBefore() {
    return searchBefore;
  }

  public void setSearchBefore(List<String> searchBefore) {
    this.searchBefore = searchBefore;
  }

  public List<String> getSearchBeforeOrEqual() {
    return searchBeforeOrEqual;
  }

  public void setSearchBeforeOrEqual(List<String> searchBeforeOrEqual) {
    this.searchBeforeOrEqual = searchBeforeOrEqual;
  }
}
