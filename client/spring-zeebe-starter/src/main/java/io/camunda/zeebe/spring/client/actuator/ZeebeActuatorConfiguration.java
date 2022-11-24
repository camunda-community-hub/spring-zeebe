package io.camunda.zeebe.spring.client.actuator;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.connector.MetricsRecorder;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeActuatorConfiguration {

  @Bean
  @ConditionalOnClass(EndpointAutoConfiguration.class)
  @ConditionalOnBean(MeterRegistry.class)
  public MetricsRecorder metricsRecorder(final MeterRegistry meterRegistry) {
    return new MicrometerMetricsRecorder(meterRegistry);
  }

  @Bean
  @ConditionalOnProperty(prefix = "management.health.zeebe", name = "enabled", matchIfMissing = true)
  @ConditionalOnClass(HealthIndicator.class)
  @ConditionalOnMissingBean(name = "zeebeClientHealthIndicator")
  public ZeebeClientHealthIndicator zeebeClientHealthIndicator(ZeebeClient client) {
    return new ZeebeClientHealthIndicator(client);
  }

}
