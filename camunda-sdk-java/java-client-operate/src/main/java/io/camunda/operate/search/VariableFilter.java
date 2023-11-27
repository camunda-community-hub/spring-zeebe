package io.camunda.operate.search;

import io.camunda.operate.model.Variable;

public class VariableFilter extends Variable implements Filter {

  public VariableFilterBuilder builder() { return new VariableFilterBuilder(); }

}
