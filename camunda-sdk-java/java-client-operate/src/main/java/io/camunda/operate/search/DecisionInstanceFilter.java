package io.camunda.operate.search;

import io.camunda.operate.model.DecisionInstance;

public class DecisionInstanceFilter extends DecisionInstance implements Filter {

  public static DecisionInstanceFilterBuilder builder() {
    return new DecisionInstanceFilterBuilder();
  }
}
