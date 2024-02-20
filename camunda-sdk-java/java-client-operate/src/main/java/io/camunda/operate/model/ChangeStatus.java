package io.camunda.operate.model;

public class ChangeStatus {

  private String message;
  private Long deleted;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getDeleted() {
    return deleted;
  }

  public void setDeleted(Long deleted) {
    this.deleted = deleted;
  }
}
