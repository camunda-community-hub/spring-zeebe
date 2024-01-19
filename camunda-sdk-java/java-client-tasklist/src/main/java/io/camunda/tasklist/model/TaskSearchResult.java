package io.camunda.tasklist.model;

import java.util.List;

public class TaskSearchResult extends Task {

  private List<String> sortValues;
  private Boolean isFirst;

  public List<String> getSortValues() {
    return sortValues;
  }

  public void setSortValues(List<String> sortValues) {
    this.sortValues = sortValues;
  }

  public Boolean getFirst() {
    return isFirst;
  }

  public void setFirst(Boolean first) {
    isFirst = first;
  }
}
