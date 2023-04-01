package io.camunda.zeebe.spring.client.properties;


import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;import io.camunda.zeebe.spring.client.connector.Filter.ZeebeConnectorFilter;import java.util.Map;


public class PropertyBasedZeebeConnectorFilter implements ZeebeConnectorFilter{

  private final ZeebeClientConfigurationProperties zeebeClientConfigurationProperties;

  public PropertyBasedZeebeConnectorFilter(ZeebeClientConfigurationProperties zeebeClientConfigurationProperties) {
      this.zeebeClientConfigurationProperties=zeebeClientConfigurationProperties;
  }

  @Override
  public boolean disable(OutboundConnectorConfiguration connector) {
    if(Boolean.FALSE.equals(zeebeClientConfigurationProperties.getConnector().isEnabled()))
      return true;

    Map<String,ZeebeWorkerValue> connectorProperties=zeebeClientConfigurationProperties.getConnector().getProperties();

    if(connectorProperties.containsKey(connector.getName()) && Boolean.FALSE.equals(connectorProperties.get(connector.getName()).getEnabled()))
      return true;
    return false;
    }
}
