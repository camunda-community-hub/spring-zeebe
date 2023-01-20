package io.camunda.connector.runtime.inbound.correlation.result;

import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.runtime.inbound.correlation.MessageCorrelationPoint;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;

public class MessageInboundConnectorResult extends InboundConnectorResult {
  protected PublishMessageResponse responseData;

  @Override
  public PublishMessageResponse getResponseData() {
    return responseData;
  }

  public MessageInboundConnectorResult(
    PublishMessageResponse publishMessageResponse, String correlationKey) {
    super(MessageCorrelationPoint.TYPE_NAME, correlationKey, publishMessageResponse);
  }
}
