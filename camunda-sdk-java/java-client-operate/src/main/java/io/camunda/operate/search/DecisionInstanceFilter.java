package io.camunda.operate.search;

import io.camunda.operate.model.DecisionInstance;

public class DecisionInstanceFilter extends DecisionInstance implements Filter {

  public DecisionInstanceFilterBuilder builder() { return new DecisionInstanceFilterBuilder(); }

}
