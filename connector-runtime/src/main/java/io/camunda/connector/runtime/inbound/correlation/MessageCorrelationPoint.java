package io.camunda.connector.runtime.inbound.correlation;

import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties of a message published by an Inbound Connector
 */
public class MessageCorrelationPoint extends ProcessCorrelationPoint {

  public static final String TYPE_NAME = "MESSAGE";

  private static final Logger LOG = LoggerFactory.getLogger(MessageCorrelationPoint.class);
  private final String messageName;
  private final String correlationKey;

  public MessageCorrelationPoint(long processDefinitionKey, String bpmnProcessId, int version,
    String messageName, String correlationKey) {
    super(bpmnProcessId, version, processDefinitionKey);
    this.messageName = messageName;
    this.correlationKey = correlationKey;
    LOG.debug("Created inbound correlation point: " + this);
  }

  public String getMessageName() {
    return messageName;
  }

  public String getCorrelationKey() {
    return correlationKey;
  }

  @Override
  public String toString() {
    return "MessageCorrelationPoint{" +
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
