package io.zeebe.spring.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;

@Import(ZeebeActuatorConfiguration.class)
@EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
@Configuration
public class ZeebeClientStarterAutoConfiguration {

  private final ZeebeClientConfigurationProperties configurationProperties;

  public ZeebeClientStarterAutoConfiguration(ZeebeClientConfigurationProperties configurationProperties) {
    this.configurationProperties = configurationProperties;
  }

  @Bean
  @Primary
  public ZeebeClientBuilder builder() {
    final ZeebeClientBuilderImpl builder = new ZeebeClientBuilderImpl();

    builder.gatewayAddress(configurationProperties.getGatewayAddress());
    builder.defaultJobPollInterval(configurationProperties.getDefaultJobPollInterval());
    builder.defaultJobTimeout(configurationProperties.getDefaultJobTimeout());
    builder.defaultJobWorkerMaxJobsActive(configurationProperties.getDefaultJobWorkerMaxJobsActive());
    builder.defaultJobWorkerName(configurationProperties.getDefaultJobWorkerName());
    builder.defaultMessageTimeToLive(configurationProperties.getDefaultMessageTimeToLive());
    builder.numJobWorkerExecutionThreads(configurationProperties.getNumJobWorkerExecutionThreads());
    builder.defaultRequestTimeout(configurationProperties.getDefaultRequestTimeout());
    builder.credentialsProvider(configurationProperties.getCredentialsProvider());
    builder.caCertificatePath(configurationProperties.getCaCertificatePath());
    if (configurationProperties.isPlaintextConnectionEnabled())
      builder.usePlaintext();
    return builder;
  }
}
