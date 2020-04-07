package io.zeebe.spring.client.config;

import io.zeebe.client.ZeebeClient;
import io.zeebe.spring.client.actuator.ZeebeClientHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "management.health.zeebe", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(HealthIndicator.class)
public class ZeebeActuatorConfiguration {

  @Bean
  @ConditionalOnMissingBean(name = "zeebeClientHealthIndicator")
  public ZeebeClientHealthIndicator zeebeClientHealthIndicator(ZeebeClient client) {
    return new ZeebeClientHealthIndicator(client);
  }

}
