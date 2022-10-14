package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.properties.PropertyBasedZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.grpc.ClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.List;

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
  public ZeebeClientBuilder builder(@Autowired(required = false) @Qualifier(value = "zeebeObjectMapper") JsonMapper jsonMapper,
                                    @Autowired(required = false) List<ClientInterceptor> clientInterceptorList) {
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
    if (jsonMapper==null) { // double security because of https://github.com/camunda-community-hub/spring-zeebe/issues/240
      jsonMapper = zeebeObjectMapper();
    }
    builder.withJsonMapper(jsonMapper);
    final List<ClientInterceptor> legacyInterceptors = configurationProperties.getInterceptors();
    if (!legacyInterceptors.isEmpty()) {
      builder.withInterceptors(legacyInterceptors.toArray(new ClientInterceptor[0]));
    } else if (clientInterceptorList != null && !clientInterceptorList.isEmpty()) {
      builder.withInterceptors(clientInterceptorList.toArray(new ClientInterceptor[0]));
    }
    return builder;
  }

  @Bean("propertyBasedZeebeWorkerValueCustomizer")
  @ConditionalOnMissingBean(name = "propertyBasedZeebeWorkerValueCustomizer")
  public ZeebeWorkerValueCustomizer propertyBasedZeebeWorkerValueCustomizer() {
    return new PropertyBasedZeebeWorkerValueCustomizer(this.configurationProperties);
  }

  @Bean(name = "zeebeObjectMapper")
  @ConditionalOnMissingBean(name = "zeebeObjectMapper")
  public JsonMapper zeebeObjectMapper() {
    return new ZeebeObjectMapper();
  }
}
