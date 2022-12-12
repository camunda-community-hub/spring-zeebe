package io.camunda.zeebe.spring.client.actuator;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.metrics.DefaultNoopMetricsRecorder;
import io.camunda.zeebe.spring.client.metrics.MetricsDefaultConfiguration;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(MetricsDefaultConfiguration.class)
@ConditionalOnClass({EndpointAutoConfiguration.class, MeterRegistry.class}) // only if actuator is on classpath
public class ZeebeActuatorConfiguration {
  @Bean
  public MetricsRecorder micrometerMetricsRecorder(
          // ConditionalOnBean does not work, because the MetricsRecorder is created too late
          final @Autowired(required = false) MeterRegistry meterRegistry) {

    if (meterRegistry == null) {
      // We might have Actuator on the classpath without starting a MetricsRecorder in some cases
      return new DefaultNoopMetricsRecorder();
    } else {
      return new MicrometerMetricsRecorder(meterRegistry);
    }
  }

  @Bean
  @ConditionalOnProperty(prefix = "management.health.zeebe", name = "enabled", matchIfMissing = true)
  @ConditionalOnClass(HealthIndicator.class)
  @ConditionalOnMissingBean(name = "zeebeClientHealthIndicator")
  public ZeebeClientHealthIndicator zeebeClientHealthIndicator(ZeebeClient client) {
    return new ZeebeClientHealthIndicator(client);
  }
}
