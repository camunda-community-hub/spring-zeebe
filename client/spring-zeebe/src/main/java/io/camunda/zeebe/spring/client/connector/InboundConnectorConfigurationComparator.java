package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;

import java.util.Comparator;

public class InboundConnectorConfigurationComparator implements Comparator<InboundConnectorConfiguration> {

  @Override
  public int compare(InboundConnectorConfiguration o1, InboundConnectorConfiguration o2) {
    if (o1 == o2) return 0;
    if (o1 == null) return -1;

    return o1.getType().compareTo(o2.getType());
  }

}
