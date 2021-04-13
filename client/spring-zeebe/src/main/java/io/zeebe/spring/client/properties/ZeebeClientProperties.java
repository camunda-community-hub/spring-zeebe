package io.zeebe.spring.client.properties;

import io.grpc.ClientInterceptor;
import io.zeebe.client.CredentialsProvider;
import java.time.Duration;
import java.util.List;

public interface ZeebeClientProperties {

  @Deprecated
  default String getBrokerContactPoint() {
    return getGatewayAddress();
  }

  String getGatewayAddress();

  int getNumJobWorkerExecutionThreads();

  int getDefaultJobWorkerMaxJobsActive();

  String getDefaultJobWorkerName();

  Duration getDefaultJobTimeout();

  Duration getDefaultJobPollInterval();

  Duration getDefaultMessageTimeToLive();

  Duration getDefaultRequestTimeout();

  boolean isPlaintextConnectionEnabled();

  String getCaCertificatePath();

  CredentialsProvider getCredentialsProvider();

  Duration getKeepAlive();

  /**
   * This method and configuration is deprecated. You should declare beans on type {@link ClientInterceptor} at your Spring context and they will be used. See:
   * {@link io.zeebe.spring.client.config.ZeebeClientStarterAutoConfiguration#builder(io.zeebe.client.api.JsonMapper, java.util.List)}
   *
   * @deprecated
   * @return list of GRPC interceptors.
   */
  @Deprecated
  List<ClientInterceptor> getInterceptors();

  default boolean isAutoStartup() {
    return true;
  }
}
