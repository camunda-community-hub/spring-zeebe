package io.camunda.operate.search;

public class ProcessDefinitionFilterBuilder {

  ProcessDefinitionFilter filter;

  ProcessDefinitionFilterBuilder() {
    filter = new ProcessDefinitionFilter();
  }

  public ProcessDefinitionFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public ProcessDefinitionFilterBuilder name(String name) {
    filter.setName(name);
    return this;
  }

  public ProcessDefinitionFilterBuilder version(Long version) {
    filter.setVersion(version);
    return this;
  }

  public ProcessDefinitionFilterBuilder bpmnProcessId(String bpmnProcessId) {
    filter.setBpmnProcessId(bpmnProcessId);
    return this;
  }

  public ProcessDefinitionFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public ProcessDefinitionFilter build() {
    return filter;
  }
}
