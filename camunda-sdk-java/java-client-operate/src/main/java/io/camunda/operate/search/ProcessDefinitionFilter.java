package io.camunda.operate.search;

import io.camunda.operate.model.ProcessDefinition;

public class ProcessDefinitionFilter extends ProcessDefinition implements Filter {

  public static ProcessDefinitionFilterBuilder builder() {
    return new ProcessDefinitionFilterBuilder();
  }
}
