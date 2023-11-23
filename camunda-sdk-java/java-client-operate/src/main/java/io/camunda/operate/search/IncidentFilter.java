package io.camunda.operate.search;

import io.camunda.operate.model.Incident;

public class IncidentFilter extends Incident implements Filter {

  public IncidentFilterBuilder builder() { return new IncidentFilterBuilder(); }

}
