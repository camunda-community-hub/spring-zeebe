package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientImpl;
import io.camunda.zeebe.client.impl.util.ExecutorResource;
import io.camunda.zeebe.gateway.protocol.GatewayGrpc;
import io.camunda.zeebe.spring.client.configuration.condition.ZeebeClientCondition;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/*
 * All configurations that will only be used in production code - meaning NO TEST cases
 */
@Conditional(ZeebeClientCondition.class)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@ImportAutoConfiguration({
  ExecutorServiceConfiguration.class,
  ZeebeActuatorConfiguration.class,
  JsonMapperConfiguration.class,
  AuthenticationConfiguration.class
})
@AutoConfigureBefore(ZeebeClientAllAutoConfiguration.class)
public class ZeebeClientProdAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Bean
  public ZeebeClientConfiguration zeebeClientConfiguration(
      ZeebeClientConfigurationProperties properties,
      CamundaClientProperties camundaClientProperties,
      Authentication authentication,
      JsonMapper jsonMapper,
      List<ClientInterceptor> interceptors,
      ZeebeClientExecutorService zeebeClientExecutorService) {
    return new ZeebeClientConfiguration(
        properties,
        camundaClientProperties,
        authentication,
        jsonMapper,
        interceptors,
        zeebeClientExecutorService);
  }

  @Bean(destroyMethod = "close")
  public ZeebeClient zeebeClient(
      final ZeebeClientConfiguration configuration) { // (ZeebeClientBuilder builder) {
    // LOG.info("Creating ZeebeClient using ZeebeClientBuilder [" + builder + "]");
    // return builder.build();

    LOG.info("Creating ZeebeClient using ZeebeClientConfiguration [" + configuration + "]");
    final ScheduledExecutorService jobWorkerExecutor = configuration.jobWorkerExecutor();
    if (jobWorkerExecutor != null) {
      ManagedChannel managedChannel = ZeebeClientImpl.buildChannel(configuration);
      GatewayGrpc.GatewayStub gatewayStub =
          ZeebeClientImpl.buildGatewayStub(managedChannel, configuration);
      ExecutorResource executorResource =
          new ExecutorResource(jobWorkerExecutor, configuration.ownsJobWorkerExecutor());
      return new ZeebeClientImpl(configuration, managedChannel, gatewayStub, executorResource);
    } else {
      return new ZeebeClientImpl(configuration);
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
