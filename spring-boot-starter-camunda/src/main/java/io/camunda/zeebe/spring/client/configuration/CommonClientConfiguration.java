package io.camunda.zeebe.spring.client.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.common.auth.*;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import io.camunda.zeebe.spring.client.properties.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
  CommonConfigurationProperties.class,
  ZeebeSelfManagedProperties.class
})
public class CommonClientConfiguration {

  @Autowired(required = false)
  CommonConfigurationProperties commonConfigurationProperties;

  @Autowired(required = false)
  ZeebeClientConfigurationProperties zeebeClientConfigurationProperties;

  @Autowired(required = false)
  ConsoleClientConfigurationProperties consoleClientConfigurationProperties;

  @Autowired(required = false)
  OptimizeClientConfigurationProperties optimizeClientConfigurationProperties;

  @Autowired(required = false)
  TasklistClientConfigurationProperties tasklistClientConfigurationProperties;

  @Autowired(required = false)
  OperateClientConfigurationProperties operateClientConfigurationProperties;

  @Autowired(required = false)
  ZeebeSelfManagedProperties zeebeSelfManagedProperties;



  @Bean
  public Authentication authentication(JsonMapper jsonMapper) {

    // TODO: Refactor
    if (zeebeClientConfigurationProperties != null) {
      // check if Zeebe has clusterId provided, then must be SaaS
      if (zeebeClientConfigurationProperties.getCloud().getClusterId() != null) {
        return SaaSAuthentication.builder()
            .withJwtConfig(configureJwtConfig())
            .withJsonMapper(jsonMapper)
            .build();
      } else if (zeebeClientConfigurationProperties.getBroker().getGatewayAddress() != null
          || zeebeSelfManagedProperties.getGatewayAddress() != null) {
        // figure out if Self-Managed JWT or Self-Managed Basic
        if (operateClientConfigurationProperties != null) {
          if (operateClientConfigurationProperties.getKeycloakUrl() != null) {
            return SelfManagedAuthentication.builder()
                .withJwtConfig(configureJwtConfig())
                .withKeycloakUrl(operateClientConfigurationProperties.getKeycloakUrl())
                .withKeycloakRealm(operateClientConfigurationProperties.getKeycloakRealm())
                .withJsonMapper(jsonMapper)
                .build();
          } else if (operateClientConfigurationProperties.getKeycloakTokenUrl() != null) {
            return SelfManagedAuthentication.builder()
                .withJwtConfig(configureJwtConfig())
                .withKeycloakTokenUrl(operateClientConfigurationProperties.getKeycloakTokenUrl())
                .withJsonMapper(jsonMapper)
                .build();
          } else if (operateClientConfigurationProperties.getUsername() != null
              && operateClientConfigurationProperties.getPassword() != null) {
            SimpleConfig simpleConfig = new SimpleConfig();
            SimpleCredential simpleCredential =
                new SimpleCredential(
                    operateClientConfigurationProperties.getUsername(),
                    operateClientConfigurationProperties.getPassword());
            simpleConfig.addProduct(Product.OPERATE, simpleCredential);
            return SimpleAuthentication.builder()
                .withSimpleConfig(simpleConfig)
                .withSimpleUrl(operateClientConfigurationProperties.getUrl())
                .build();
          }
        }

        if (commonConfigurationProperties != null) {
          if (commonConfigurationProperties.getKeycloak().getUrl() != null) {
            return SelfManagedAuthentication.builder()
                .withJwtConfig(configureJwtConfig())
                .withKeycloakUrl(commonConfigurationProperties.getKeycloak().getUrl())
                .withKeycloakRealm(commonConfigurationProperties.getKeycloak().getRealm())
                .withJsonMapper(jsonMapper)
                .build();
          } else if (commonConfigurationProperties.getKeycloak().getTokenUrl() != null) {
            return SelfManagedAuthentication.builder()
                .withJwtConfig(configureJwtConfig())
                .withKeycloakTokenUrl(commonConfigurationProperties.getKeycloak().getTokenUrl())
                .withJsonMapper(jsonMapper)
                .build();
          } else if (commonConfigurationProperties.getUsername() != null
              && commonConfigurationProperties.getPassword() != null) {
            SimpleConfig simpleConfig = new SimpleConfig();
            SimpleCredential simpleCredential =
                new SimpleCredential(
                    commonConfigurationProperties.getUsername(),
                    commonConfigurationProperties.getPassword());
            simpleConfig.addProduct(Product.OPERATE, simpleCredential);
            return SimpleAuthentication.builder()
                .withSimpleConfig(simpleConfig)
                .withSimpleUrl(commonConfigurationProperties.getUrl())
                .build();
          }
        }
      }
    }
    return new DefaultNoopAuthentication();
  }

  private JwtConfig configureJwtConfig() {
    JwtConfig jwtConfig = new JwtConfig();
    // ZEEBE
    if (zeebeClientConfigurationProperties.getCloud().getClientId() != null
        && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
      jwtConfig.addProduct(
          Product.ZEEBE,
          new JwtCredential(
              zeebeClientConfigurationProperties.getCloud().getClientId(),
              zeebeClientConfigurationProperties.getCloud().getClientSecret(),
              zeebeClientConfigurationProperties.getCloud().getAudience(),
              zeebeClientConfigurationProperties.getCloud().getAuthUrl()));
    } else if (zeebeSelfManagedProperties.getClientId() != null
        && zeebeSelfManagedProperties.getClientSecret() != null) {
      jwtConfig.addProduct(
          Product.ZEEBE,
          new JwtCredential(
              zeebeSelfManagedProperties.getClientId(),
              zeebeSelfManagedProperties.getClientSecret(),
              zeebeSelfManagedProperties.getAudience(),
              zeebeSelfManagedProperties.getAuthServer()));
    } else if (commonConfigurationProperties.getClientId() != null
        && commonConfigurationProperties.getClientSecret() != null) {
      jwtConfig.addProduct(
          Product.ZEEBE,
          new JwtCredential(
              commonConfigurationProperties.getClientId(),
              commonConfigurationProperties.getClientSecret(),
              zeebeClientConfigurationProperties.getCloud().getAudience(),
              zeebeClientConfigurationProperties.getCloud().getAuthUrl()));
    }

    // OPERATE
    String operateAuthUrl = zeebeClientConfigurationProperties.getCloud().getAuthUrl();
    String operateAudience = "operate.camunda.io";
    if (operateClientConfigurationProperties != null) {
      if (operateClientConfigurationProperties.getAuthUrl() != null) {
        operateAuthUrl = operateClientConfigurationProperties.getAuthUrl();
      }
      if (operateClientConfigurationProperties.getBaseUrl() != null) {
        operateAudience = operateClientConfigurationProperties.getBaseUrl();
      }
      if (operateClientConfigurationProperties.getClientId() != null
          && operateClientConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(
            Product.OPERATE,
            new JwtCredential(
                operateClientConfigurationProperties.getClientId(),
                operateClientConfigurationProperties.getClientSecret(),
                operateAudience,
                operateAuthUrl));
      } else if (commonConfigurationProperties.getClientId() != null
          && commonConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(
            Product.OPERATE,
            new JwtCredential(
                commonConfigurationProperties.getClientId(),
                commonConfigurationProperties.getClientSecret(),
                operateAudience,
                operateAuthUrl));
      } else if (zeebeClientConfigurationProperties.getCloud().getClientId() != null
          && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
        jwtConfig.addProduct(
            Product.OPERATE,
            new JwtCredential(
                zeebeClientConfigurationProperties.getCloud().getClientId(),
                zeebeClientConfigurationProperties.getCloud().getClientSecret(),
                operateAudience,
                operateAuthUrl));
      } else if (zeebeSelfManagedProperties.getClientId() != null
          && zeebeSelfManagedProperties.getClientSecret() != null) {
        jwtConfig.addProduct(
            Product.OPERATE,
            new JwtCredential(
                zeebeSelfManagedProperties.getClientId(),
                zeebeSelfManagedProperties.getClientSecret(),
                operateAudience,
                operateAuthUrl));
      } else {
        throw new RuntimeException("Unable to determine OPERATE credentials");
      }
    }
    return jwtConfig;
  }
}
