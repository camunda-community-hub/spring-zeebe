package io.camunda.connector.runtime.inbound.util.command;

import io.camunda.connector.runtime.inbound.util.response.PublishMessageResponseDummy;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.command.PublishMessageCommandStep1;
import io.camunda.zeebe.client.api.command.PublishMessageCommandStep1.PublishMessageCommandStep2;
import io.camunda.zeebe.client.api.command.PublishMessageCommandStep1.PublishMessageCommandStep3;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.impl.ZeebeClientFutureImpl;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

public class PublishMessageCommandDummy
  implements PublishMessageCommandStep1,
  PublishMessageCommandStep2,
  PublishMessageCommandStep3 {

  @Override
  public PublishMessageCommandStep2 messageName(String messageName) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 correlationKey(String correlationKey) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 messageId(String messageId) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 timeToLive(Duration timeToLive) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 variables(InputStream variables) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 variables(String variables) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 variables(Map<String, Object> variables) {
    return this;
  }

  @Override
  public PublishMessageCommandStep3 variables(Object variables) {
    return this;
  }

  @Override
  public FinalCommandStep<PublishMessageResponse> requestTimeout(Duration requestTimeout) {
    return this;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public ZeebeFuture<PublishMessageResponse> send() {
    ZeebeClientFutureImpl future = new ZeebeClientFutureImpl<>();
    future.complete(new PublishMessageResponseDummy());
    return future;
  }
}
