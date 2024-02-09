package io.camunda.operate.search;

import io.camunda.operate.model.DecisionRequirements;

public class DecisionRequirementsFilter extends DecisionRequirements implements Filter {

  public static DecisionRequirementsFilterBuilder builder() {
    return new DecisionRequirementsFilterBuilder();
  }
}
