package io.camunda.zeebe.spring.client.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@ConfigurationProperties(prefix = "zeebe")
@Deprecated
public class ZeebeSelfManagedProperties {

  @Value("${zeebe.authorization.server.url:#{null}}")
  private String authServer;

  @Value("${zeebe.client.id:#{null}}")
  private String clientId;

  @Value("${zeebe.client.secret:#{null}}")
  private String clientSecret;

  @Value("${zeebe.token.audience:#{null}}")
  private String audience;

  @Value("${zeebe.client.broker.gatewayAddress:#{null}}")
  private String gatewayAddress;

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.issuer")
  @Deprecated
  public String getAuthServer() {
    return authServer;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.client-id")
  @Deprecated
  public String getClientId() {
    return clientId;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.client-secret")
  @Deprecated
  public String getClientSecret() {
    return clientSecret;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.audience")
  @Deprecated
  public String getAudience() {
    return audience;
  }

  @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.base-url")
  @Deprecated
  public String getGatewayAddress() {
    return gatewayAddress;
  }
}
