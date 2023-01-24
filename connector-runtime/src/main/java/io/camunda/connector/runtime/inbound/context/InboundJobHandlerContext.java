package io.camunda.connector.runtime.inbound.context;

import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.context.AbstractConnectorContext;
import io.camunda.connector.runtime.inbound.correlation.MessageCorrelationPoint;
import io.camunda.connector.runtime.inbound.correlation.StartEventCorrelationPoint;
import io.camunda.connector.runtime.inbound.correlation.result.MessageInboundConnectorResult;
import io.camunda.connector.runtime.inbound.correlation.result.StartEventInboundConnectorResult;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundJobHandlerContext extends AbstractConnectorContext implements InboundConnectorContext {
  private static final Logger LOG = LoggerFactory.getLogger(InboundJobHandlerContext.class);

  private final ZeebeClient zeebeClient;

  public InboundJobHandlerContext(
    SecretProvider secretProvider,
    ZeebeClient zeebeClient) {
    super(secretProvider);
    this.zeebeClient = zeebeClient;
  }

  @Override
  public InboundConnectorResult correlate(ProcessCorrelationPoint correlationPoint, Map<String, Object> variables) {

    if (correlationPoint instanceof StartEventCorrelationPoint) {
      return triggerStartEvent((StartEventCorrelationPoint) correlationPoint, variables);
    }
    if (correlationPoint instanceof MessageCorrelationPoint) {
      return triggerMessage((MessageCorrelationPoint) correlationPoint, variables);
    }
    throw new ConnectorException(
      "Process correlation point " + correlationPoint.getClass() + " is not supported by Runtime");
  }

  private InboundConnectorResult triggerStartEvent(
    StartEventCorrelationPoint correlationPoint, Map<String, Object> variables) {
    try {
      ProcessInstanceEvent result = zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId(correlationPoint.getBpmnProcessId())
        .version(correlationPoint.getVersion())
        .variables(variables)
        .send()
        .join();

      LOG.info("Created a process instance with key" + result.getProcessInstanceKey());
      return new StartEventInboundConnectorResult(result);

    } catch (Exception e) {
      throw new ConnectorException(
        "Failed to start process instance via StartEvent: " + correlationPoint, e);
    }
  }

  private InboundConnectorResult triggerMessage(
    MessageCorrelationPoint correlationPoint, Map<String, Object> variables) {
    try {
      PublishMessageResponse response = zeebeClient.newPublishMessageCommand()
        .messageName(correlationPoint.getMessageName())
        .correlationKey(correlationPoint.getCorrelationKey())
        .variables(variables)
        .send()
        .join();

      LOG.info("Published message with key: " + response.getMessageKey());
      return new MessageInboundConnectorResult(response, correlationPoint.getCorrelationKey());

    } catch (Exception e) {
      throw new ConnectorException(
        "Failed to publish process message for subscription: " + correlationPoint, e);
    }
  }
}
