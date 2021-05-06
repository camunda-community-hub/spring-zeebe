package io.camunda.spring.client.config;

import io.grpc.ClientInterceptor;
import io.camunda.zeebe.client.api.JsonMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.spring.client.properties.ZeebeClientConfigurationProperties;

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
  public ZeebeClientBuilder builder(
    @Autowired(required = false) JsonMapper jsonMapper,
    @Autowired(required = false) List<ClientInterceptor> clientInterceptorList
  ) {
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
    if (configurationProperties.isPlaintextConnectionEnabled()) {
      builder.usePlaintext();
    }
    if (jsonMapper != null) {
      builder.withJsonMapper(jsonMapper);
    }
    final List<ClientInterceptor> legacyInterceptors = configurationProperties.getInterceptors();
    if (!legacyInterceptors.isEmpty()) {
      builder.withInterceptors(legacyInterceptors.toArray(new ClientInterceptor[0]));
    } else if (clientInterceptorList != null && !clientInterceptorList.isEmpty()) {
      builder.withInterceptors(clientInterceptorList.toArray(new ClientInterceptor[0]));
    }
    return builder;
  }
}
