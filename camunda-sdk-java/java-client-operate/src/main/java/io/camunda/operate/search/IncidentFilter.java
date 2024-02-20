package io.camunda.operate.search;

import io.camunda.operate.model.Incident;

public class IncidentFilter extends Incident implements Filter {

  public static IncidentFilterBuilder builder() {
    return new IncidentFilterBuilder();
  }
}
