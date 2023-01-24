package io.camunda.connector.runtime.inbound.correlation;

import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties of a StartEvent triggered by an Inbound Connector
 */
public class StartEventCorrelationPoint extends ProcessCorrelationPoint {

  public static final String TYPE_NAME = "START_EVENT";

  private static final Logger LOG = LoggerFactory.getLogger(StartEventCorrelationPoint.class);

  public StartEventCorrelationPoint(long processDefinitionKey, String bpmnProcessId, int version) {
    super(bpmnProcessId, version, processDefinitionKey);
    LOG.debug("Created inbound correlation point: " + this);
  }

  @Override
  public String toString() {
    return "StartEventCorrelationPoint{" +
      "processDefinitionKey=" + processDefinitionKey +
      ", bpmnProcessId='" + bpmnProcessId + '\'' +
      ", version=" + version +
      '}';
  }

  @Override
  public int compareTo(ProcessCorrelationPoint o) {
    if (!this.getClass().equals(o.getClass())) {
      return -1;
    }
    StartEventCorrelationPoint other = (StartEventCorrelationPoint) o;
    if (!bpmnProcessId.equals(other.bpmnProcessId)) {
      return bpmnProcessId.compareTo(other.bpmnProcessId);
    }
    return Integer.compare(version, other.version);
  }
}
