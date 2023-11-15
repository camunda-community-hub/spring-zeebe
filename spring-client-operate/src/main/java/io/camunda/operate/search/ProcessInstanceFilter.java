package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.exception.OperateException;

public class ProcessInstanceFilter extends ProcessInstance implements Filter {

  public ProcessInstanceFilterBuilder builder() { return new ProcessInstanceFilterBuilder(); }

}
