package io.camunda.connector.runtime.inbound.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.api.inbound.InboundConnectorTarget;
import io.camunda.connector.api.inbound.InboundConnectorWrapperException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inbound connector is mapped to a StartEvent
 */
public class StartEventInboundTarget extends InboundConnectorTarget {
  private static final Logger LOG = LoggerFactory.getLogger(StartEventInboundTarget.class);
  private final ZeebeClient zeebeClient;

  public StartEventInboundTarget(long processDefinitionKey, String bpmnProcessId, int version, ZeebeClient zeebeClient) {
    super(bpmnProcessId, version, processDefinitionKey);
    this.zeebeClient = zeebeClient;
    LOG.debug("Registered a StartEventInboundTarget: " + this);
  }

  @Override
  public Response triggerEvent(Map<String, Object> variables) {
    try {
      ProcessInstanceEvent result = zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId(bpmnProcessId)
        .version(version)
        .variables(variables)
        .send()
        .join();
      LOG.debug("Created a process instance with key" + result.getProcessInstanceKey());
      return new Response(result);
    } catch (Exception e) {
      // TODO: with details
      throw new InboundConnectorWrapperException("oops", e);
    }
  }

  @Override
  public String toString() {
    return "StartEvent{" +
      "processDefinitionKey=" + processDefinitionKey +
      ", bpmnProcessId='" + bpmnProcessId + '\'' +
      ", version=" + version +
      '}';
  }

  @Override
  public int compareTo(InboundConnectorTarget o) {
    if (!this.getClass().equals(o.getClass())) {
      return -1;
    }
    StartEventInboundTarget other = (StartEventInboundTarget) o;
    if (!bpmnProcessId.equals(other.bpmnProcessId)) {
      return bpmnProcessId.compareTo(other.bpmnProcessId);
    }
    return Integer.compare(version, other.version);
  }

  @JsonTypeName("START_EVENT")
  public static class Response extends InboundConnectorResult {

    private final ProcessInstanceEvent processInstanceEvent;

    public Response(final ProcessInstanceEvent processInstanceEvent) {
      this.processInstanceEvent = processInstanceEvent;
    }

    public ProcessInstanceEvent getProcessInstanceEvent() {
      return this.processInstanceEvent;
    }

    @Override
    public String toString() {
      return "Response{" +
        "processInstanceEvent=" + processInstanceEvent +
        '}';
    }
  }
}
