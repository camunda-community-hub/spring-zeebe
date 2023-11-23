package io.camunda.operate.search;

import io.camunda.operate.model.ProcessInstance;

public class ProcessInstanceFilter extends ProcessInstance implements Filter {

  public ProcessInstanceFilterBuilder builder() { return new ProcessInstanceFilterBuilder(); }

}
