package io.camunda.zeebe.spring.client.event;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Camunda agnostic TaskReceivedEvent which is handed to the Dynamically registered listeners.
 * @author Sai.
 */
public class TaskReceivedEvent extends ApplicationEvent {
  private final String key;
  private final String processId;
  private final String processVersion;
  private final String elementId;
  private final String worker;
  private final Map<String, Object> variables;
  private final Map<String, String> headers;

  public TaskReceivedEvent(String key, String processId, String processVersion,
                           String elementId, String worker, Map<String, Object> variables, Map<String, String> headers) {
    super(key);
    this.key = key;
    this.processId = processId;
    this.elementId = elementId;
    this.worker = worker;
    this.variables = variables;
    this.headers = headers;
    this.processVersion = processVersion;
  }

  public String getKey() {
    return key;
  }

  public String getProcessId() {
    return processId;
  }

  public String getProcessVersion() {
    return processVersion;
  }

  public String getElementId() {
    return elementId;
  }

  public String getWorker() {
    return worker;
  }

  public Map<String, Object> getVariables() {
    return new HashMap<>(variables);
  }

  public Map<String, String> getHeaders() {
    return new HashMap<>(headers);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskReceivedEvent that = (TaskReceivedEvent) o;
    return Objects.equals(key, that.key) && Objects.equals(processId, that.processId) && Objects.equals(processVersion, that.processVersion) && Objects.equals(elementId, that.elementId) && Objects.equals(worker, that.worker) && Objects.equals(variables, that.variables) && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, processId, processVersion, elementId, worker, variables, headers);
  }
}
