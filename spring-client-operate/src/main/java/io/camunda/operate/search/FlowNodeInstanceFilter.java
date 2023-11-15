package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.model.FlowNodeInstance;
import io.camunda.operate.model.FlowNodeInstanceState;
import io.camunda.operate.exception.OperateException;

public class FlowNodeInstanceFilter extends FlowNodeInstance implements Filter {

  public FlowNodeInstanceFilterBuilder builder() { return new FlowNodeInstanceFilterBuilder(); }

}
