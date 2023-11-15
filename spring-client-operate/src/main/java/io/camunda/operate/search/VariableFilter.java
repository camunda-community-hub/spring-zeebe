package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.Variable;

public class VariableFilter extends Variable implements Filter {

  public VariableFilterBuilder builder() { return new VariableFilterBuilder(); }

}
