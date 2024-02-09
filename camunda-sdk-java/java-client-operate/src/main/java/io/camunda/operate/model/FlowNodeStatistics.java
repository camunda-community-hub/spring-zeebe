package io.camunda.operate.model;

public class FlowNodeStatistics {

  private String activityId;
  private Long active;
  private Long canceled;
  private Long incidents;
  private Long completed;

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public Long getActive() {
    return active;
  }

  public void setActive(Long active) {
    this.active = active;
  }

  public Long getCanceled() {
    return canceled;
  }

  public void setCanceled(Long canceled) {
    this.canceled = canceled;
  }

  public Long getIncidents() {
    return incidents;
  }

  public void setIncidents(Long incidents) {
    this.incidents = incidents;
  }

  public Long getCompleted() {
    return completed;
  }

  public void setCompleted(Long completed) {
    this.completed = completed;
  }
}
