package io.camunda.zeebe.spring.client.properties;

import io.grpc.ClientInterceptor;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.JsonMapper;

import java.util.List;
import java.util.Map;

public interface ZeebeClientProperties extends ZeebeClientConfiguration {

  @Deprecated
  default String getBrokerContactPoint() {
    return getGatewayAddress();
  }

  /**
   * This method and configuration is deprecated in the Spring environment and shall not be used.
   * Declare beans on type {@link JsonMapper} in your Spring context and they will be used automatically.
   * See: ZeebeClientStarterAutoConfiguration#builder
   *
   * @deprecated
   * @return list of GRPC interceptors.
   */
  @Deprecated
  List<ClientInterceptor> getInterceptors();

  /**
   * This method and configuration is deprecated in the Spring environment and shall not be used.
   * Declare beans on type {@link JsonMapper} in your Spring context and they will be used automatically.
   *
   * @deprecated
   */
  @Deprecated
  JsonMapper getJsonMapper();

  default boolean isAutoStartup() {
    return true;
  }

  /**
   * This method returns a worker's configuration for its specific type.
   * So, it helps to disable workers via configuration
   * in the {@link io.camunda.zeebe.spring.client.annotation.value.factory.ReadZeebeWorkerValue#apply(io.camunda.zeebe.spring.client.bean.MethodInfo)} method.
   *
   * @return A map with an association of worker's type and a worker's configuration.
   */
  Map<String, WorkerConfiguration> getWorkersConfiguration();

  /**
   * This class contains a configuration that could override the common configuration. Right now it contains only the enabled property,
   * but it could be extended to support all configurations from the {@link io.camunda.zeebe.spring.client.annotation.ZeebeWorker} annotation.
   */
  class WorkerConfiguration {
    private boolean enabled;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
