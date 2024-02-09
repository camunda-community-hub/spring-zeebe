package io.camunda.operate.search;

import io.camunda.operate.model.DecisionDefinition;

public class DecisionDefinitionFilter extends DecisionDefinition implements Filter {

  public static DecisionDefinitionFilterBuilder builder() {
    return new DecisionDefinitionFilterBuilder();
  }
}
