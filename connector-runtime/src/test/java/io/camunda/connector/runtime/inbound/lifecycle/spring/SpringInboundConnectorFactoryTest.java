package io.camunda.connector.runtime.inbound.lifecycle.spring;

import io.camunda.connector.impl.ConnectorUtil;
import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.runtime.ConnectorRuntimeApplication;
import io.camunda.connector.runtime.inbound.TestInboundConnector;
import io.camunda.connector.runtime.inbound.lifecycle.SpringInboundConnectorFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
  classes = {ConnectorRuntimeApplication.class},
  properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "camunda.connector.webhook.enabled=false"
  })
@ExtendWith(MockitoExtension.class)
public class SpringInboundConnectorFactoryTest {

  @Autowired
  private SpringInboundConnectorFactory factory;

  @Test
  void shouldDiscoverConnectorFromSpringContext() {

    // given: 1 connector from SPI and 2 from Spring context
    var spiConnectorConfig = ConnectorUtil
      .getRequiredInboundConnectorConfiguration(TestInboundConnector.class);
    var firstSpringConnectorConfig = ConnectorUtil
      .getRequiredInboundConnectorConfiguration(
        ConnectorsConfiguration.FirstInboundConnector.class);
    var secondSpringConnectorConfig = ConnectorUtil
      .getRequiredInboundConnectorConfiguration(
        ConnectorsConfiguration.SecondInboundConnector.class);

    // when

    List<InboundConnectorConfiguration> registeredConnectors =
      factory.getConfigurations();

    // then

    assertEquals(3, registeredConnectors.size());
    assertTrue(registeredConnectors.containsAll(
      List.of(
        firstSpringConnectorConfig,
        secondSpringConnectorConfig,
        spiConnectorConfig)));

    // check that spring connectors are singletons

    var firstInstance = factory.getInstance(firstSpringConnectorConfig.getType());
    var secondInstance = factory.getInstance(firstSpringConnectorConfig.getType());

    assertSame(firstInstance, secondInstance);

    // check that SPI connectors are disposable

    firstInstance = factory.getInstance(spiConnectorConfig.getType());
    secondInstance = factory.getInstance(spiConnectorConfig.getType());

    assertNotSame(firstInstance, secondInstance);
  }
}
