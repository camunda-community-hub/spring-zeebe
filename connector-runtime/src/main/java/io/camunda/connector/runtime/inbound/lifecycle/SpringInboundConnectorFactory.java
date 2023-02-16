package io.camunda.connector.runtime.inbound.lifecycle;

import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.impl.ConnectorUtil;
import io.camunda.connector.impl.inbound.InboundConnectorConfiguration;
import io.camunda.connector.runtime.util.inbound.DefaultInboundConnectorFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link io.camunda.connector.runtime.util.inbound.InboundConnectorFactory} implementation
 * that supports discovering Connectors defined as Spring beans.
 * Connectors that are defined as Spring beans are created by Spring.
 */
@Component
public class SpringInboundConnectorFactory extends DefaultInboundConnectorFactory {
  private final static Logger LOG = LoggerFactory.getLogger(SpringInboundConnectorFactory.class);

  private final Map<String, InboundConnectorExecutable> springConnectorsByType = new HashMap<>();

  public SpringInboundConnectorFactory(
    @Autowired(required = false) List<InboundConnectorExecutable> connectorsFromSpringContext) {
    super(); // run default connector discovery mechanisms

    if (connectorsFromSpringContext == null) {
      LOG.info("No inbound connectors are declared as Spring beans");
      return;
    }
    for (InboundConnectorExecutable connector : connectorsFromSpringContext) {
      var cls = connector.getClass();
      InboundConnectorConfiguration config = ConnectorUtil
        .getRequiredInboundConnectorConfiguration(cls);

      LOG.info("Registering inbound connector declared as Spring bean: " + config);
      registerConfiguration(config);
      springConnectorsByType.put(config.getType(), connector);
    }
  }

  @Override
  public InboundConnectorExecutable getInstance(String type) {
    if (springConnectorsByType.containsKey(type)) {
      return springConnectorsByType.get(type);
    }
    return super.getInstance(type);
  }
}
