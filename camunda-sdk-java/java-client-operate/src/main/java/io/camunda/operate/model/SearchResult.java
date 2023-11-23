package io.camunda.operate.model;

import java.util.Iterator;
import java.util.List;

public class SearchResult<T> implements Iterable<T> {
  private List<T> items;

  private Integer total;

  private List<Object> sortValues;

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public List<Object> getSortValues() {
    return sortValues;
  }

  public void setSortValues(List<Object> sortValues) {
    this.sortValues = sortValues;
  }

  public Iterator<T> iterator() {
    return this.items.iterator();
  }
}
