package io.camunda.zeebe.spring.client.configuration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientImpl;
import io.camunda.zeebe.gateway.protocol.GatewayGrpc;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;

public class ZeebeClientConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ZeebeClientConfigurationProperties configurationProperties;
  private final ZeebeClientExecutorService zeebeClientExecutorService;

  public ZeebeClientConfiguration(ZeebeClientConfigurationProperties configurationProperties, ZeebeClientExecutorService zeebeClientExecutorService, JsonMapper jsonMapper) {
    this.configurationProperties = configurationProperties;
    this.zeebeClientExecutorService = zeebeClientExecutorService;
  }

  @Bean(destroyMethod = "close")
  public ZeebeClient zeebeClient() { // (ZeebeClientBuilder builder) {
    //LOG.info("Creating ZeebeClient using ZeebeClientBuilder [" + builder + "]");
    //return builder.build();

    LOG.info("Creating ZeebeClient using ZeebeClientConfiguration [" + configurationProperties + "]");
    if (zeebeClientExecutorService!=null) {
      ManagedChannel managedChannel = ZeebeClientImpl.buildChannel(configurationProperties);
      GatewayGrpc.GatewayStub gatewayStub = ZeebeClientImpl.buildGatewayStub(managedChannel, configurationProperties);
      return new ZeebeClientImpl(configurationProperties, managedChannel, gatewayStub, zeebeClientExecutorService.get());
    } else {
      return new ZeebeClientImpl(configurationProperties);
    }
  }
  // TODO: Interceptors
  // TODO: applyOverrides()
/*
  @Bean
  public ZeebeClientBuilder builder(JsonMapper jsonMapper,
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
    builder.withJsonMapper(jsonMapper);
    final List<ClientInterceptor> legacyInterceptors = configurationProperties.getInterceptors();
    if (!legacyInterceptors.isEmpty()) {
      builder.withInterceptors(legacyInterceptors.toArray(new ClientInterceptor[0]));
    } else if (clientInterceptorList != null && !clientInterceptorList.isEmpty()) {
      builder.withInterceptors(clientInterceptorList.toArray(new ClientInterceptor[0]));
    }
    return builder;
  }*/


}
