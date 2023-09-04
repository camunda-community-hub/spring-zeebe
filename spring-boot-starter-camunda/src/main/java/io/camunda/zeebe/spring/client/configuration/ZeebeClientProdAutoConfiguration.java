package io.camunda.zeebe.spring.client.configuration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientImpl;
import io.camunda.zeebe.client.impl.util.ExecutorResource;
import io.camunda.zeebe.gateway.protocol.GatewayGrpc;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;

/*
 * All configurations that will only be used in production code - meaning NO TEST cases
 */
@ConditionalOnProperty(prefix = "zeebe.client", name = "enabled", havingValue = "true",  matchIfMissing = true)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@ImportAutoConfiguration({
  ExecutorServiceConfiguration.class, ZeebeActuatorConfiguration.class
})
@AutoConfigureBefore(ZeebeClientAllAutoConfiguration.class)
public class ZeebeClientProdAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ZeebeClientConfigurationProperties configurationProperties;
  private final ZeebeClientExecutorService zeebeClientExecutorService;

  public ZeebeClientProdAutoConfiguration(ZeebeClientConfigurationProperties configurationProperties, ZeebeClientExecutorService zeebeClientExecutorService, JsonMapper jsonMapper) {
    this.configurationProperties = configurationProperties;
    configurationProperties.setJsonMapper(jsonMapper); // Replace JsonMapper proxy (because of lazy) with real bean
    configurationProperties.applyOverrides(); // make sure environment variables and other legacy config options are taken into account (duplicate, also done by  qPostConstruct, whatever)
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
      ExecutorResource executorResource = new ExecutorResource(zeebeClientExecutorService.get(), configurationProperties.ownsJobWorkerExecutor());
      return new ZeebeClientImpl(configurationProperties, managedChannel, gatewayStub, executorResource);
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
