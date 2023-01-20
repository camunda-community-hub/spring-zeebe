package io.camunda.connector.runtime.inbound.correlation;

import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCorrelationPoint extends ProcessCorrelationPoint {

  public static final String TYPE_NAME = "MESSAGE";

  private static final Logger LOG = LoggerFactory.getLogger(MessageCorrelationPoint.class);
  private final String messageName;
  private final String correlationKey;

  public MessageCorrelationPoint(String bpmnProcessId, int version, long processDefinitionKey,
    String messageName, String correlationKey) {
    super(bpmnProcessId, version, processDefinitionKey);
    this.messageName = messageName;
    this.correlationKey = correlationKey;
    LOG.debug("Registered a MessageInboundTarget: " + this);
  }

  public String getMessageName() {
    return messageName;
  }

  public String getCorrelationKey() {
    return correlationKey;
  }

  @Override
  public String toString() {
    return "IntermediateEvent{" +
      "messageName='" + messageName + '\'' +
      ", correlationKey='" + correlationKey + '\'' +
      '}';
  }

  @Override
  public int compareTo(ProcessCorrelationPoint o) {
    if (!this.getClass().equals(o.getClass())) {
      return -1;
    }
    MessageCorrelationPoint other = (MessageCorrelationPoint) o;
    return correlationKey.compareTo(other.correlationKey);
  }
}
