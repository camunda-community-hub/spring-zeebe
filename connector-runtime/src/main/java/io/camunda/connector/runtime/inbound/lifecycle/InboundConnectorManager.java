package io.camunda.connector.runtime.inbound.lifecycle;

import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.importer.ProcessDefinitionInspector;
import io.camunda.connector.runtime.util.inbound.InboundConnectorContextImpl;
import io.camunda.connector.runtime.util.inbound.InboundConnectorFactory;
import io.camunda.connector.runtime.util.inbound.correlation.InboundCorrelationHandler;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InboundConnectorManager {
  private static final Logger LOG = LoggerFactory.getLogger(InboundConnectorManager.class);

  private final InboundConnectorFactory connectorFactory;
  private final InboundCorrelationHandler correlationHandler;
  private final ProcessDefinitionInspector processDefinitionInspector;
  private final SecretProvider secretProvider;

  // TODO: consider using external storage instead of these collections to allow multi-instance setup
  private final Set<Long> registeredProcessDefinitionKeys = new HashSet<>();
  private final Map<String, InboundConnectorExecutable> activeConnectorsByCorrelationPointId =
    new ConcurrentHashMap<>();
  private final Map<String, Set<InboundConnectorProperties>> activeConnectorsByBpmnId = new HashMap<>();

  public InboundConnectorManager(
    InboundConnectorFactory connectorFactory,
    InboundCorrelationHandler correlationHandler,
    ProcessDefinitionInspector processDefinitionInspector,
    SecretProvider secretProvider) {
    this.connectorFactory = connectorFactory;
    this.correlationHandler = correlationHandler;
    this.processDefinitionInspector = processDefinitionInspector;
    this.secretProvider = secretProvider;
  }

  /**
   * Check whether process definition with provided key is already registered
   */
  public boolean isProcessDefinitionRegistered(long processDefinitionKey) {
    return registeredProcessDefinitionKeys.contains(processDefinitionKey);
  }

  /**
   * Process a batch of process definitions
   */
  public void registerProcessDefinitions(List<ProcessDefinition> processDefinitions) throws OperateException {
    if (processDefinitions == null || processDefinitions.isEmpty()) {
      return;
    }
    var newProcDefs = processDefinitions.stream()
      // register unregistered proc definitions
      .filter(procDef -> !isProcessDefinitionRegistered(procDef.getKey()))
      .peek(procDef -> registeredProcessDefinitionKeys.add(procDef.getKey()))
      // group by BPMN ID
      .collect(Collectors.groupingBy(ProcessDefinition::getBpmnProcessId));

    // we will only handle the latest versions of process defs for each BPMN ID
    var relevantProcDefs = newProcDefs.values().stream()
      .map(
        list -> Collections.max(list, Comparator.comparing(ProcessDefinition::getVersion)))
      .collect(Collectors.toList());

    for (ProcessDefinition procDef : relevantProcDefs) {
      handleLatestBpmnVersion(
        procDef.getBpmnProcessId(),
        processDefinitionInspector.findInboundConnectors(procDef));
    }
  }

  public Map<String, Set<InboundConnectorProperties>> getActiveConnectorsByBpmnId() {
    return activeConnectorsByBpmnId;
  }

  private void handleLatestBpmnVersion(String bpmnId, List<InboundConnectorProperties> connectors) {
    var alreadyActiveConnectors = activeConnectorsByBpmnId.get(bpmnId);
    if (alreadyActiveConnectors != null) {
      alreadyActiveConnectors.forEach(this::deactivateConnector);
    }
    connectors.forEach(this::activateConnector);
  }

  private void deactivateConnector(InboundConnectorProperties properties) {
    InboundConnectorExecutable executable =
      activeConnectorsByCorrelationPointId.get(properties.getCorrelationPointId());

    if (executable == null) {
      throw new IllegalStateException("Connector executable not found for properties " + properties);
    }
    try {
      executable.deactivate();
      activeConnectorsByCorrelationPointId.remove(properties.getCorrelationPointId());
      activeConnectorsByBpmnId.get(properties.getBpmnProcessId()).remove(properties);
    } catch (Exception e) {
      // log and continue with other connectors anyway
      LOG.error("Failed to deactivate inbound connector " + properties, e);
    }
  }

  private void activateConnector(InboundConnectorProperties newProperties) {

    InboundConnectorExecutable executable = connectorFactory.getInstance(newProperties.getType());

    try {
      executable.activate(
        new InboundConnectorContextImpl(secretProvider, newProperties, correlationHandler));

      activeConnectorsByCorrelationPointId.put(newProperties.getCorrelationPointId(), executable);
      activeConnectorsByBpmnId.compute(
        newProperties.getBpmnProcessId(),
        (bpmnId, connectorPropertiesSet) -> {
          if (connectorPropertiesSet == null) {
            Set<InboundConnectorProperties> set = new HashSet<>();
            set.add(newProperties);
            return set;
          }
          connectorPropertiesSet.add(newProperties);
          return connectorPropertiesSet;
        });
    } catch (Exception e) {
      // log and continue with other connectors anyway
      LOG.error("Failed to activate inbound connector " + newProperties, e);
    }
  }
}
