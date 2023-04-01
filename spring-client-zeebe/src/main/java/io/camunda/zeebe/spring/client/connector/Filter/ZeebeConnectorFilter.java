package io.camunda.zeebe.spring.client.connector.Filter;


import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;

/***
 * This interface supports customize the rule that is used to disable outbound connector
 * Implements this interface and override to {@link this.disable(connector)}.
 * When the {@link this.disable(connector)} return ture,the connector will be disabled.
 * Be careful: these filters are applied sequentially and if you need to change the order of these customizers use the {@link org.springframework.core.annotation.Order} annotation or the {@link org.springframework.core.Ordered} interface.
 *
 */

public interface ZeebeConnectorFilter {

  public  boolean disable(OutboundConnectorConfiguration connector);
}
