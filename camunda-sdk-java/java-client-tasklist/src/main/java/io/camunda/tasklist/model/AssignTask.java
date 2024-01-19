package io.camunda.tasklist.model;

public class AssignTask {

  private String assignee;
  private Boolean allowOverrideAssignment;

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public Boolean getAllowOverrideAssignment() {
    return allowOverrideAssignment;
  }

  public void setAllowOverrideAssignment(Boolean allowOverrideAssignment) {
    this.allowOverrideAssignment = allowOverrideAssignment;
  }
}
