package io.camunda.connector.runtime.inbound.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.api.inbound.InboundConnectorTarget;
import io.camunda.connector.api.inbound.InboundConnectorWrapperException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageInboundTarget extends InboundConnectorTarget {
  private static final Logger LOG = LoggerFactory.getLogger(MessageInboundTarget.class);

  private final String messageName;
  private final String correlationKey;
  private final ZeebeClient zeebeClient;

  public MessageInboundTarget(String bpmnProcessId, int version, long processDefinitionKey,
    String messageName, String correlationKey, ZeebeClient zeebeClient) {
    super(bpmnProcessId, version, processDefinitionKey);
    this.messageName = messageName;
    this.correlationKey = correlationKey;
    this.zeebeClient = zeebeClient;
    LOG.debug("Registered a MessageInboundTarget: " + this);
  }

  @Override
  public Response triggerEvent(Map<String, Object> variables) {
    try {
      PublishMessageResponse response = zeebeClient.newPublishMessageCommand()
        .messageName(messageName)
        .correlationKey(correlationKey)
        .variables(variables)
        .send()
        .join();
      return new Response(response, messageName, correlationKey);
    } catch (Exception e) {
      // TODO: with details
      throw new InboundConnectorWrapperException("oops", e);
    }
  }

  @Override
  public String toString() {
    return "IntermediateEvent{" +
      "messageName='" + messageName + '\'' +
      ", correlationKey='" + correlationKey + '\'' +
      '}';
  }

  @Override
  public int compareTo(InboundConnectorTarget o) {
    if (!this.getClass().equals(o.getClass())) {
      return -1;
    }
    MessageInboundTarget other = (MessageInboundTarget) o;
    if (!correlationKey.equals(other.correlationKey)) {
      return correlationKey.compareTo(other.correlationKey);
    }
    return messageName.compareTo(other.messageName);
  }

  @JsonTypeName("MESSAGE")
  public static class Response extends InboundConnectorResult {

    private final String messageName;
    private final String correlationKey;
    private final PublishMessageResponse publishMessageResponse;

    public Response(PublishMessageResponse publishMessageResponse, String messageName, String correlationKey) {
      this.messageName = messageName;
      this.correlationKey = correlationKey;
      this.publishMessageResponse = publishMessageResponse;
    }

    public String getMessageName() {
      return messageName;
    }

    public String getCorrelationKey() {
      return correlationKey;
    }

    public PublishMessageResponse getPublishMessageResponse() {
      return publishMessageResponse;
    }

    @Override
    public String toString() {
      return "Response{" +
        "messageName='" + messageName + '\'' +
        ", correlationKey='" + correlationKey + '\'' +
        ", publishMessageResponse=" + publishMessageResponse +
        '}';
    }
  }
}
