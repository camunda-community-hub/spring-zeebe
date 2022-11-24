package io.camunda.zeebe.spring.client.actuator;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "management.health.zeebe", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(HealthIndicator.class)
@ConditionalOnMissingBean(name = "zeebeClientHealthIndicator")
public class ZeebeClientHealthIndicator extends AbstractHealthIndicator {

  private final ZeebeClient client;

  @Autowired
  public ZeebeClientHealthIndicator(ZeebeClient client) {
    this.client = client;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) {
    Topology topology = client.newTopologyRequest().send().join();
    if (topology.getBrokers().isEmpty()) {
      builder.down();
    } else {
      builder.up();
    }
  }

}
