package io.camunda.connector.runtime.inbound.lifecycle;

import java.util.Map;
import java.util.Objects;

public class ActiveInboundConnectorResponse {
  private final String bpmnProcessId;
  private final String type;
  private final Map<String, Object> data;

  public ActiveInboundConnectorResponse(
    final String bpmnProcessId, final String type, final Map<String, Object> data) {

    this.bpmnProcessId = bpmnProcessId;
    this.type = type;
    this.data = data;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public String getType() {
    return type;
  }

  public Map<String, Object> getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ActiveInboundConnectorResponse that = (ActiveInboundConnectorResponse) o;
    return Objects.equals(bpmnProcessId, that.bpmnProcessId) && Objects.equals(
      type, that.type) && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bpmnProcessId, type, data);
  }

  @Override
  public String toString() {
    return "InboundConnectorResponse{" +
      "bpmnProcessId='" + bpmnProcessId + '\'' +
      ", type='" + type + '\'' +
      ", data=" + data +
      '}';
  }
}
