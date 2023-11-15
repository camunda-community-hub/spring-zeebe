package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.model.Incident;

public class IncidentFilter extends Incident implements Filter {

  public IncidentFilterBuilder builder() { return new IncidentFilterBuilder(); }

}
