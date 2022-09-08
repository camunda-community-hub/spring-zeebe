package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.JsonMapper;
import io.grpc.ClientInterceptor;

import java.util.List;

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
}
