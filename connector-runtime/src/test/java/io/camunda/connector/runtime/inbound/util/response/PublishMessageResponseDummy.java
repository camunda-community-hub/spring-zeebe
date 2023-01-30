package io.camunda.connector.runtime.inbound.util.response;

import io.camunda.zeebe.client.api.response.PublishMessageResponse;

public class PublishMessageResponseDummy implements PublishMessageResponse {
  @Override
  public long getMessageKey() {
    return 0;
  }
}
