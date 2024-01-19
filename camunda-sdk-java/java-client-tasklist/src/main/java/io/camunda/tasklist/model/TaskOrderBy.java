package io.camunda.tasklist.model;

public class TaskOrderBy {

  private String field;
  private String order;
  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }
}
