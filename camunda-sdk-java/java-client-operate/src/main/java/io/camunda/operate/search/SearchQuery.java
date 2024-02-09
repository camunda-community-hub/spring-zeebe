package io.camunda.operate.search;

import io.camunda.operate.exception.OperateException;
import java.util.ArrayList;
import java.util.List;

public class SearchQuery {
  private Filter filter;
  private Integer size;
  private List<Sort> sort;
  private List<Object> searchAfter;

  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public List<Sort> getSort() {
    return sort;
  }

  public void setSort(List<Sort> sort) {
    this.sort = sort;
  }

  public List<Object> getSearchAfter() {
    return searchAfter;
  }

  public void setSearchAfter(List<Object> searchAfter) {
    this.searchAfter = searchAfter;
  }

  public static class Builder {

    private Filter filter;
    private Integer size;
    private List<Sort> sorts = new ArrayList<>();
    private List<Object> searchAfter = null;

    public Builder() {}

    @Deprecated
    public Builder withFilter(Filter filter) {
      this.filter = filter;
      return this;
    }

    public Builder filter(Filter filter) {
      this.filter = filter;
      return this;
    }

    @Deprecated
    public Builder withSize(Integer size) {
      this.size = size;
      return this;
    }

    public Builder size(Integer size) {
      this.size = size;
      return this;
    }

    @Deprecated
    public Builder withSort(Sort sort) {
      this.sorts.add(sort);
      return this;
    }

    public Builder sort(Sort sort) {
      this.sorts.add(sort);
      return this;
    }

    public Builder searchAfter(List<Object> searchAfter) {
      this.searchAfter = searchAfter;
      return this;
    }

    public SearchQuery build() throws OperateException {
      SearchQuery query = new SearchQuery();
      query.filter = filter;
      query.size = size;
      query.searchAfter = searchAfter;
      if (!sorts.isEmpty()) {
        query.setSort(sorts);
      }
      return query;
    }
  }
}
