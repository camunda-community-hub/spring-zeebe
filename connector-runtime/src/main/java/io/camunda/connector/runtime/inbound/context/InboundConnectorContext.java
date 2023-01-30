package io.camunda.connector.runtime.inbound.context;

import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.context.AbstractConnectorContext;
import io.camunda.connector.impl.inbound.MessageCorrelationPoint;
import io.camunda.connector.impl.inbound.StartEventCorrelationPoint;
import io.camunda.connector.runtime.inbound.correlation.MessageInboundConnectorResult;
import io.camunda.connector.runtime.inbound.correlation.StartEventInboundConnectorResult;
import io.camunda.connector.runtime.util.feel.FeelEngineWrapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundConnectorContext extends AbstractConnectorContext implements
  io.camunda.connector.api.inbound.InboundConnectorContext {
  private static final Logger LOG = LoggerFactory.getLogger(InboundConnectorContext.class);

  private final ZeebeClient zeebeClient;

  private final FeelEngineWrapper feelEngine;

  public InboundConnectorContext(
    SecretProvider secretProvider,
    ZeebeClient zeebeClient, FeelEngineWrapper feelEngine) {
    super(secretProvider);
    this.zeebeClient = zeebeClient;
    this.feelEngine = feelEngine;
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

    String correlationKey = feelEngine.evaluate(correlationPoint.getCorrelationKeyExpression(), variables);

    try {
      PublishMessageResponse response = zeebeClient.newPublishMessageCommand()
        .messageName(correlationPoint.getMessageName())
        .correlationKey(correlationKey)
        .variables(variables)
        .send()
        .join();

      LOG.info("Published message with key: " + response.getMessageKey());
      return new MessageInboundConnectorResult(response, correlationKey);

    } catch (Exception e) {
      throw new ConnectorException(
        "Failed to publish process message for subscription: " + correlationPoint, e);
    }
  }
}
