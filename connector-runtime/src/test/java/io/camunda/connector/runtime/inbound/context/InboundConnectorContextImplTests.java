package io.camunda.connector.runtime.inbound.context;

import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.impl.inbound.MessageCorrelationPoint;
import io.camunda.connector.impl.inbound.StartEventCorrelationPoint;
import io.camunda.connector.runtime.inbound.util.InboundConnectorContextBuilder;
import io.camunda.connector.runtime.inbound.util.command.CreateCommandDummy;
import io.camunda.connector.runtime.inbound.util.command.PublishMessageCommandDummy;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InboundConnectorContextImplTests {

  private ZeebeClient zeebeClient;
  private InboundConnectorContext connectorContext;

  @BeforeEach
  public void initMock() {
    zeebeClient = mock(ZeebeClient.class);
    connectorContext = InboundConnectorContextBuilder.create()
      .zeebeClient(zeebeClient)
      .build();
  }

  @Test
  public void startEvent_shouldCallCorrectZeebeMethod() {
    // given
    var point = new StartEventCorrelationPoint(0, "process1", 0);
    Map<String, Object> variables = Map.of("testKey", "testValue");

    var dummyCommand = spy(new CreateCommandDummy());
    when(zeebeClient.newCreateInstanceCommand()).thenReturn(dummyCommand);

    // when
    connectorContext.correlate(point, variables);

    // then
    verify(zeebeClient).newCreateInstanceCommand();
    verifyNoMoreInteractions(zeebeClient);

    verify(dummyCommand).bpmnProcessId(point.getBpmnProcessId());
    verify(dummyCommand).version(point.getVersion());
    verify(dummyCommand).variables(variables);
    verify(dummyCommand).send();
  }

  @Test
  public void message_shouldCallCorrectZeebeMethod() {
    // given
    var correlationKeyValue = "someTestCorrelationKeyValue";
    var point = new MessageCorrelationPoint("msg1", "=correlationKey");
    Map<String, Object> variables = Map.of("correlationKey", correlationKeyValue);

    var dummyCommand = spy(new PublishMessageCommandDummy());
    when(zeebeClient.newPublishMessageCommand()).thenReturn(dummyCommand);

    // when
    connectorContext.correlate(point, variables);

    // then
    verify(zeebeClient).newPublishMessageCommand();
    verifyNoMoreInteractions(zeebeClient);

    verify(dummyCommand).messageName(point.getMessageName());
    verify(dummyCommand).correlationKey(correlationKeyValue);
    verify(dummyCommand).variables(variables);
    verify(dummyCommand).send();
  }
}
