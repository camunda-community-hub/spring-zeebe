package io.camunda.operate.search;

import io.camunda.operate.model.FlowNodeInstance;

public class FlowNodeInstanceFilter extends FlowNodeInstance implements Filter {

  public static FlowNodeInstanceFilterBuilder builder() {
    return new FlowNodeInstanceFilterBuilder();
  }
}
