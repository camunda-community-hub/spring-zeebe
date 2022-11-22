package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;

import java.util.Comparator;

public class OutboundConnectorConfigurationComparator implements Comparator<OutboundConnectorConfiguration> {

  @Override
  public int compare(OutboundConnectorConfiguration o1, OutboundConnectorConfiguration o2) {
    if (o1 == o1) return 0;
    if (o1 == null) return -1;

    return o1.getType().compareTo(o2.getType());
  }

}
