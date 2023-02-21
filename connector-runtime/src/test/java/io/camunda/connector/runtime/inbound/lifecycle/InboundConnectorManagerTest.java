package io.camunda.connector.runtime.inbound.lifecycle;

import static io.camunda.connector.runtime.inbound.ProcessDefinitionTestUtil.processDefinition;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.impl.ConnectorUtil;
import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import io.camunda.connector.impl.inbound.correlation.MessageCorrelationPoint;
import io.camunda.connector.runtime.inbound.TestInboundConnector;
import io.camunda.connector.runtime.inbound.importer.ProcessDefinitionInspector;
import io.camunda.connector.runtime.inbound.ProcessDefinitionTestUtil;
import io.camunda.connector.runtime.util.inbound.InboundConnectorContextImpl;
import io.camunda.connector.runtime.util.inbound.InboundConnectorFactory;
import io.camunda.connector.runtime.util.inbound.correlation.InboundCorrelationHandler;
import io.camunda.operate.dto.ProcessDefinition;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InboundConnectorManagerTest {
  /*
   see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
   */

  private InboundConnectorManager manager;
  private ProcessDefinitionTestUtil procDefUtil;
  private InboundConnectorFactory factory;
  private InboundConnectorExecutable mockExecutable;
  private SecretProvider secretProvider;
  private InboundCorrelationHandler correlationHandler;


  @BeforeEach
  void resetMocks() {
    correlationHandler = mock(InboundCorrelationHandler.class);

    mockExecutable = spy(new TestInboundConnector());
    factory = mock(InboundConnectorFactory.class);
    when(factory.getInstance(any())).thenReturn(mockExecutable);

    secretProvider = mock(SecretProvider.class);

    ProcessDefinitionInspector inspector = mock(ProcessDefinitionInspector.class);

    manager = new InboundConnectorManager(factory, correlationHandler, inspector, secretProvider);
    procDefUtil = new ProcessDefinitionTestUtil(manager, inspector);
  }

  @Test
  void shouldActivateConnector_NewBpmnDeployed_SingleConnector() throws Exception {
    // given
    var process = processDefinition("proc1", 1);
    var connector = inboundConnector(process);

    // when
    procDefUtil.deployProcessDefinition(process, connector);

    // then
    assertTrue(manager.isProcessDefinitionRegistered(process.getKey()));
    verify(factory, times(1)).getInstance(connector.getType());
    verify(mockExecutable, times(1))
      .activate(eq(inboundContext(connector)));
  }

  @Test
  void shouldActivateConnector_NewBpmnDeployed_MultipleConnectors() throws Exception {
    // given
    var process = processDefinition("proc1", 1);
    var connectors = List.of(
      inboundConnector(process), inboundConnector(process));

    // when
    procDefUtil.deployProcessDefinition(process, connectors);

    // then
    assertTrue(manager.isProcessDefinitionRegistered(process.getKey()));
    verify(factory, times(2)).getInstance(connectors.get(0).getType());
    for (var connector : connectors) {
      verify(mockExecutable, times(1)).
        activate(eq(inboundContext(connector)));
    }
  }

  @Test
  void shouldReplaceConnector_NewVersionDeployed() throws Exception {
    // given
    var process1 = processDefinition("proc1", 1);
    var connector1 = inboundConnector(process1);
    var process2 = processDefinition("proc1", 2);
    var connector2 = inboundConnector(process2);

    // when
    procDefUtil.deployProcessDefinition(process1, connector1);
    procDefUtil.deployProcessDefinition(process2, connector2);

    // then
    assertTrue(manager.isProcessDefinitionRegistered(process1.getKey()));
    assertTrue(manager.isProcessDefinitionRegistered(process2.getKey()));

    verify(factory, times(2)).getInstance(connector1.getType());

    verify(mockExecutable, times(1)).activate(eq(inboundContext(connector1)));
    verify(mockExecutable, times(1)).deactivate();
    verify(mockExecutable, times(1)).activate(eq(inboundContext(connector2)));
    verifyNoMoreInteractions(mockExecutable);
  }

  @Test
  void shouldNotActivate_NewBpmnDeployed_NoConnectors() throws Exception {
    // given
    var process = processDefinition("proc1", 1);

    // when
    procDefUtil.deployProcessDefinition(process, Collections.emptyList());

    // then
    assertTrue(manager.isProcessDefinitionRegistered(process.getKey()));
    verifyNoInteractions(factory);
    verifyNoInteractions(mockExecutable);
  }

  @Test
  void shouldOnlyActivateLatestConnectors_BulkImport() throws Exception {
    // given
    var process1 = processDefinition("proc1", 1);
    var connector1 = inboundConnector(process1);
    var process2 = processDefinition("proc1", 2);
    var connector2 = inboundConnector(process2);

    // when
    // emulates import of historic data, when old process definitions exists
    // that were replaced before the runtime was even started
    procDefUtil.deployProcessDefinition(
      Map.of(
        process1, List.of(connector1),
        process2, List.of(connector2)));

    // then
    assertTrue(manager.isProcessDefinitionRegistered(process1.getKey()));

    assertTrue(manager.isProcessDefinitionRegistered(process2.getKey()));
    verify(factory, times(1)).getInstance(connector2.getType());
    verify(mockExecutable, times(1)).activate(inboundContext(connector2));

    verifyNoMoreInteractions(factory);
    verifyNoMoreInteractions(mockExecutable);
  }

  private InboundConnectorContext inboundContext(InboundConnectorProperties properties) {
    return new InboundConnectorContextImpl(secretProvider, properties, correlationHandler);
  }

  private final static InboundConnectorConfiguration connectorConfig = ConnectorUtil
    .getRequiredInboundConnectorConfiguration(TestInboundConnector.class);

  private static InboundConnectorProperties inboundConnector(ProcessDefinition procDef) {
    return new InboundConnectorProperties(
      new MessageCorrelationPoint("", ""),
      Map.of("inbound.type", connectorConfig.getType()),
      procDef.getBpmnProcessId(),
      procDef.getVersion().intValue(),
      procDef.getKey());
  }
}
