package io.camunda.operate.search;

public class VariableFilterBuilder {

  VariableFilter filter;

  VariableFilterBuilder() {
    filter = new VariableFilter();
  }

  public VariableFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public VariableFilterBuilder processInstanceKey(Long processInstanceKey) {
    filter.setProcessInstanceKey(processInstanceKey);
    return this;
  }

  public VariableFilterBuilder scopeKey(Long scopeKey) {
    filter.setScopeKey(scopeKey);
    return this;
  }

  public VariableFilterBuilder name(String name) {
    filter.setName(name);
    return this;
  }

  public VariableFilterBuilder value(String value) {
    filter.setValue(value);
    return this;
  }

  public VariableFilterBuilder truncated(Boolean truncated) {
    filter.setTruncated(truncated);
    return this;
  }

  public VariableFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public VariableFilter build() {
    return filter;
  }
}
