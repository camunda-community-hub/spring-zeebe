package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.*;
import io.camunda.zeebe.spring.client.properties.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(CommonConfigurationProperties.class)
public class CommonClientConfiguration {


  @Autowired(required = false)
  CommonConfigurationProperties commonConfigurationProperties;

  @Autowired(required = false)
  ZeebeClientConfigurationProperties zeebeClientConfigurationProperties;

  @Autowired(required = false)
  OperateClientConfigurationProperties operateClientConfigurationProperties;

  @Autowired(required = false)
  ConsoleClientConfigurationProperties consoleClientConfigurationProperties;

  @Autowired(required = false)
  OptimizeClientConfigurationProperties optimizeClientConfigurationProperties;

  @Autowired(required = false)
  TasklistClientConfigurationProperties tasklistClientConfigurationProperties;

  // TODO: Remove below properties when we deprecate camunda.[product].client.*
  @Autowired(required = false)
  CamundaOperateClientConfigurationProperties camundaOperateClientConfigurationProperties;

  @Bean
  public Authentication authentication() {

    // TODO: Refactor
    // check if Zeebe has clusterId provided, then must be SaaS
    if (zeebeClientConfigurationProperties.getCloud().getClientId() != null) {
      return SaaSAuthentication.builder()
        .jwtConfig(configureJwtConfig())
        .build();
    } else if (zeebeClientConfigurationProperties.getBroker().getGatewayAddress() != null) {
      // figure out if Self-Managed JWT or Self-Managed Basic
      // TODO: Remove when we deprecate camunda.[product].client.*
      if (camundaOperateClientConfigurationProperties != null) {
        if (camundaOperateClientConfigurationProperties.getKeycloakUrl() != null) {
          return SelfManagedAuthentication.builder()
            .jwtConfig(configureJwtConfig())
            .keycloakUrl(camundaOperateClientConfigurationProperties.getKeycloakUrl())
            .keycloakRealm(camundaOperateClientConfigurationProperties.getKeycloakRealm())
            .build();
        } else if (camundaOperateClientConfigurationProperties.getUsername() != null && camundaOperateClientConfigurationProperties.getPassword() != null) {
          SimpleConfig simpleConfig = new SimpleConfig();
          SimpleCredential simpleCredential = new SimpleCredential(camundaOperateClientConfigurationProperties.getUsername(), camundaOperateClientConfigurationProperties.getPassword());
          simpleConfig.addProduct(Product.OPERATE, simpleCredential);
          return SimpleAuthentication.builder()
            .simpleConfig(simpleConfig)
            .simpleUrl(camundaOperateClientConfigurationProperties.getUrl())
            .build();
        }
      }

      if (commonConfigurationProperties != null) {
        if (commonConfigurationProperties.getKeycloak().getUrl() != null) {
          return SelfManagedAuthentication.builder()
            .jwtConfig(configureJwtConfig())
            .keycloakUrl(commonConfigurationProperties.getKeycloak().getUrl())
            .keycloakRealm(commonConfigurationProperties.getKeycloak().getRealm())
            .build();
        } else if (commonConfigurationProperties.getUsername() != null && commonConfigurationProperties.getPassword() != null) {
          SimpleConfig simpleConfig = new SimpleConfig();
          SimpleCredential simpleCredential = new SimpleCredential(commonConfigurationProperties.getUsername(), commonConfigurationProperties.getPassword());
          simpleConfig.addProduct(Product.OPERATE, simpleCredential);
          return SimpleAuthentication.builder()
            .simpleConfig(simpleConfig)
            .simpleUrl(commonConfigurationProperties.getUrl())
            .build();
        }
      }
    }
    return new DefaultNoopAuthentication().build();
  }

  private JwtConfig configureJwtConfig() {
    JwtConfig jwtConfig = new JwtConfig();
    if (zeebeClientConfigurationProperties.isEnabled()) {
      if (zeebeClientConfigurationProperties.getCloud().getClientId() != null && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
        jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(zeebeClientConfigurationProperties.getCloud().getClientId(), zeebeClientConfigurationProperties.getCloud().getClientSecret()));
      } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
      }
    }

    if (operateClientConfigurationProperties != null) {
      if (operateClientConfigurationProperties.getEnabled()) {
        if (operateClientConfigurationProperties.getClientId() != null && operateClientConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.OPERATE, new JwtCredential(operateClientConfigurationProperties.getClientId(), operateClientConfigurationProperties.getClientSecret()));
        } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.OPERATE, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
        }
      }
    } else if (camundaOperateClientConfigurationProperties != null) {
      // TODO: Remove this else if block when we deprecate camunda.[product].client.*
      jwtConfig.addProduct(Product.OPERATE, new JwtCredential(camundaOperateClientConfigurationProperties.getClientId(), camundaOperateClientConfigurationProperties.getClientSecret()));
    }
    return jwtConfig;
  }
}
