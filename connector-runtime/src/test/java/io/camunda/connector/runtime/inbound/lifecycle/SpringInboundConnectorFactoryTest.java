package io.camunda.connector.runtime.inbound.lifecycle;

import io.camunda.connector.impl.ConnectorUtil;
import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.runtime.ConnectorRuntimeApplication;
import io.camunda.connector.runtime.inbound.TestInboundConnector;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorExecutable;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
  classes = {ConnectorRuntimeApplication.class},
  properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "camunda.connector.webhook.enabled=true"
  })
@ExtendWith(MockitoExtension.class)
public class SpringInboundConnectorFactoryTest {

  @Autowired
  private SpringInboundConnectorFactory factory;

  @Test
  void shouldDiscoverConnectorsAndActivateWebhook() {

    var webhookConfig =
      ConnectorUtil.getRequiredInboundConnectorConfiguration(WebhookConnectorExecutable.class);
    var spiConnectorConfig = ConnectorUtil
      .getRequiredInboundConnectorConfiguration(TestInboundConnector.class);

    // when

    List<InboundConnectorConfiguration> registeredConnectors =
      factory.getConfigurations();

    // then

    assertEquals(2, registeredConnectors.size());
    assertTrue(registeredConnectors.containsAll(
      List.of(webhookConfig, spiConnectorConfig)));

    // check that SPI connectors are request-scoped

    var firstInstance = factory.getInstance(spiConnectorConfig.getType());
    var secondInstance = factory.getInstance(spiConnectorConfig.getType());

    assertNotSame(firstInstance, secondInstance);
  }
}
