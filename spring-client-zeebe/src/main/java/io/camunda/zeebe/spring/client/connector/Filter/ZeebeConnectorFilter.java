package io.camunda.zeebe.spring.client.connector.Filter;


import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;

/***
 * This abstract class supports customize the rule that is used to disable outbound connector
 * Extends this abstract class and override to {@link this.disable(connector)}.
 * When the {@link this.disable(connector)} return ture,the connector will be disabled.
 * Be careful: these filters are applied sequentially and if you need to change the order of these customizers use the {@link org.springframework.core.annotation.Order} annotation or the {@link org.springframework.core.Ordered} interface.
 *
 */

public abstract class ZeebeConnectorFilter {

  public OutboundConnectorConfiguration filter(OutboundConnectorConfiguration connector){
    if(connector==null){
      return null;
    }
    boolean checkResult=disable(connector);
    if(Boolean.TRUE.equals(checkResult))
      return null;
    return connector;
  }
  protected abstract boolean disable(OutboundConnectorConfiguration connector);
}
