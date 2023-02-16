package io.camunda.connector.runtime.inbound.util;

import static org.mockito.Mockito.when;

import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.importer.ProcessDefinitionInspector;
import io.camunda.connector.runtime.inbound.lifecycle.InboundConnectorManager;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility to mock process definition deployments and Operate API responses
 */
public class ProcessDefinitionTestUtil {

  private final ProcessDefinitionInspector inspector;
  private final InboundConnectorManager manager;

  public ProcessDefinitionTestUtil(
    InboundConnectorManager manager,
    ProcessDefinitionInspector inspector) {
    this.manager = manager;
    this.inspector = inspector;
  }

  public void deployProcessDefinition(
    ProcessDefinition processDefinition,
    InboundConnectorProperties connector) throws OperateException {
    deployProcessDefinition(processDefinition, List.of(connector));
  }


  public void deployProcessDefinition(
    ProcessDefinition processDefinition,
    List<InboundConnectorProperties> connectors) throws OperateException {

    deployProcessDefinition(Map.of(processDefinition, connectors));
  }

  public void deployProcessDefinition(
    Map<ProcessDefinition, List<InboundConnectorProperties>> connectorsByProcDef)
    throws OperateException {

    for (var entry : connectorsByProcDef.entrySet()) {
      when(inspector.findInboundConnectors(entry.getKey())).thenReturn(entry.getValue());
    }
    manager.registerProcessDefinitions(new ArrayList<>(connectorsByProcDef.keySet()));
  }

  static long processDefinitionKey = 0L;

  public static ProcessDefinition processDefinition(String bpmnId, int version) {
    ProcessDefinition pd = new ProcessDefinition();
    pd.setBpmnProcessId(bpmnId);
    pd.setVersion((long) version);
    pd.setKey(++processDefinitionKey);
    return pd;
  }
}
