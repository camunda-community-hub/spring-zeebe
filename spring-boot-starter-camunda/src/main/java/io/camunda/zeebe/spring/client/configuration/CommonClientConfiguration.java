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

  @Bean
  public Authentication authentication() {

    if (zeebeClientConfigurationProperties != null) {
      JwtConfig jwtConfig = new JwtConfig();

      if (zeebeClientConfigurationProperties.isEnabled()) {
        if (zeebeClientConfigurationProperties.getCloud().getClientId() != null && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
          jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(zeebeClientConfigurationProperties.getCloud().getClientId(), zeebeClientConfigurationProperties.getCloud().getClientSecret()));
        } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
        }
      }

      if (operateClientConfigurationProperties != null && operateClientConfigurationProperties.getEnabled()) {
        if (operateClientConfigurationProperties.getClientId() != null && operateClientConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.OPERATE, new JwtCredential(operateClientConfigurationProperties.getClientId(), operateClientConfigurationProperties.getClientSecret()));
        } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.OPERATE, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
        }
      }

      if (consoleClientConfigurationProperties != null && consoleClientConfigurationProperties.getEnabled()) {
        if (consoleClientConfigurationProperties.getClientId() != null && consoleClientConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.CONSOLE, new JwtCredential(consoleClientConfigurationProperties.getClientId(), consoleClientConfigurationProperties.getClientSecret()));
        } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.CONSOLE, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
        }
      }

      if (optimizeClientConfigurationProperties != null && optimizeClientConfigurationProperties.getEnabled()) {
        if (optimizeClientConfigurationProperties.getClientId() != null && optimizeClientConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.OPTIMIZE, new JwtCredential(optimizeClientConfigurationProperties.getClientId(), optimizeClientConfigurationProperties.getClientSecret()));
        } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.OPTIMIZE, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
        }
      }

      if (tasklistClientConfigurationProperties != null && tasklistClientConfigurationProperties.getEnabled()) {
        if (tasklistClientConfigurationProperties.getClientId() != null && tasklistClientConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.TASKLIST, new JwtCredential(tasklistClientConfigurationProperties.getClientId(), tasklistClientConfigurationProperties.getClientSecret()));
        } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
          jwtConfig.addProduct(Product.TASKLIST, new JwtCredential(commonConfigurationProperties.getClientId(), commonConfigurationProperties.getClientSecret()));
        }
      }

      if (commonConfigurationProperties != null && commonConfigurationProperties.getKeycloak().getUrl() != null) {
        return SelfManagedAuthentication.builder()
          .jwtConfig(jwtConfig)
          .keycloakUrl(commonConfigurationProperties.getKeycloak().getUrl())
          .keycloakRealm(commonConfigurationProperties.getKeycloak().getRealm())
          .build();
      } else {
        return SaaSAuthentication.builder()
          .jwtConfig(jwtConfig)
          .build();
      }
    } else if (commonConfigurationProperties != null && commonConfigurationProperties.getUser() != null) {
      SimpleConfig simpleConfig = new SimpleConfig();
      SimpleCredential common = new SimpleCredential(commonConfigurationProperties.getUser(), commonConfigurationProperties.getPassword());
      simpleConfig.addProduct(Product.OPERATE, common);
      simpleConfig.addProduct(Product.CONSOLE, common);
      simpleConfig.addProduct(Product.OPTIMIZE, common);
      simpleConfig.addProduct(Product.TASKLIST, common);
      return SimpleAuthentication.builder()
        .simpleConfig(simpleConfig)
        .build();
    } else {
      return new DefaultNoopAuthentication();
    }
  }
}
