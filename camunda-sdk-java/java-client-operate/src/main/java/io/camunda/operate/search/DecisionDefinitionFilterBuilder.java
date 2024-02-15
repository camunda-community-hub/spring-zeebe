package io.camunda.operate.search;

public class DecisionDefinitionFilterBuilder {

  DecisionDefinitionFilter filter;

  DecisionDefinitionFilterBuilder() {
    filter = new DecisionDefinitionFilter();
  }

  public DecisionDefinitionFilterBuilder id(String id) {
    filter.setId(id);
    return this;
  }

  public DecisionDefinitionFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public DecisionDefinitionFilterBuilder decisionId(String decisionId) {
    filter.setDecisionId(decisionId);
    return this;
  }

  public DecisionDefinitionFilterBuilder name(String name) {
    filter.setName(name);
    return this;
  }

  public DecisionDefinitionFilterBuilder version(Long version) {
    filter.setVersion(version);
    return this;
  }

  public DecisionDefinitionFilterBuilder decisionRequirementsId(String decisionRequirementsId) {
    filter.setDecisionRequirementsId(decisionRequirementsId);
    return this;
  }

  public DecisionDefinitionFilterBuilder decisionRequirementsKey(Long decisionRequirementsKey) {
    filter.setDecisionRequirementsKey(decisionRequirementsKey);
    return this;
  }

  public DecisionDefinitionFilterBuilder decisionRequirementsName(String decisionRequirementsName) {
    filter.setDecisionRequirementsName(decisionRequirementsName);
    return this;
  }

  public DecisionDefinitionFilterBuilder decisionRequirementsVersion(
      Long decisionRequirementsVersion) {
    filter.setDecisionRequirementsVersion(decisionRequirementsVersion);
    return this;
  }

  public DecisionDefinitionFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public DecisionDefinitionFilter build() {
    return filter;
  }
}
