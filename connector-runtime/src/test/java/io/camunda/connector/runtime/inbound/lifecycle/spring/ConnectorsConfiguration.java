package io.camunda.connector.runtime.inbound.lifecycle.spring;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorsConfiguration {

  private static abstract class AbstractInboundConnector implements InboundConnectorExecutable {

    @Override
    public void activate(InboundConnectorProperties inboundConnectorProperties,
      InboundConnectorContext inboundConnectorContext) throws Exception {

    }

    @Override
    public void deactivate(InboundConnectorProperties inboundConnectorProperties) throws Exception {

    }
  }

  @InboundConnector(name = "inbound1", type = "io.camunda:first-inbound:1")
  static class FirstInboundConnector extends AbstractInboundConnector { }

  @InboundConnector(name = "inbound2", type = "io.camunda:second-inbound:1")
  static class SecondInboundConnector extends AbstractInboundConnector { }


  @Bean
  public InboundConnectorExecutable firstConnector() {
    return new FirstInboundConnector();
  }

  @Bean
  public InboundConnectorExecutable secondConnector() {
    return new SecondInboundConnector();
  }
}
