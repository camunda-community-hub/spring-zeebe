package io.camunda.connector.runtime.inbound.correlation;

import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.impl.inbound.correlation.StartEventCorrelationPoint;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

public class StartEventInboundConnectorResult extends InboundConnectorResult {
  protected ProcessInstanceEvent responseData;

  @Override
  public ProcessInstanceEvent getResponseData() {
    return responseData;
  }

  public StartEventInboundConnectorResult(ProcessInstanceEvent processInstanceEvent) {
    super(
      StartEventCorrelationPoint.TYPE_NAME,
      String.valueOf(processInstanceEvent.getProcessDefinitionKey()),
      processInstanceEvent
    );
    this.responseData = processInstanceEvent;
  }
}
