package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.*;
import io.camunda.common.exception.SdkException;
import io.camunda.zeebe.spring.client.properties.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.*;

import static io.camunda.common.auth.Product.OPERATE;
import static io.camunda.common.auth.Product.TASKLIST;
import static io.camunda.common.auth.SelfManagedAuthenticationMode.JWT;
import static io.camunda.common.auth.SelfManagedAuthenticationMode.SIMPLE;
import static org.springframework.util.StringUtils.hasText;

@EnableConfigurationProperties({CommonConfigurationProperties.class, ZeebeSelfManagedProperties.class})
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
  TaskListClientConfigurationProperties tasklistClientConfigurationProperties;

  @Autowired(required = false)
  OperateClientConfigurationProperties operateClientConfigurationProperties;

  @Autowired(required = false)
  ZeebeSelfManagedProperties zeebeSelfManagedProperties;

  @Bean
  public Authentication authentication() {

    // TODO: Refactor
    if (zeebeClientConfigurationProperties != null) {
      // check if Zeebe has clusterId provided, then must be SaaS
      if (zeebeClientConfigurationProperties.getCloud().getClusterId() != null) {
        return SaaSAuthentication.builder()
          .jwtConfig(configureJwtConfig())
          .build();
      } else if (zeebeClientConfigurationProperties.getBroker().getGatewayAddress() != null || zeebeSelfManagedProperties.getGatewayAddress() != null) {
        // figure out if Self-Managed JWT or Self-Managed Basic
        String keycloakUrl = null;
        String keycloakRealm = null;
        String keycloakTokenUrl = null;
        Map<Product, SelfManagedAuthenticationMode> selfManagedAuthenticationModeMap = new HashMap<Product, SelfManagedAuthenticationMode>();

        if (operateClientConfigurationProperties != null) {
          if (hasText(operateClientConfigurationProperties.getKeycloakUrl()) || hasText(operateClientConfigurationProperties.getKeycloakTokenUrl())) {
            selfManagedAuthenticationModeMap.put(OPERATE, JWT);
            keycloakUrl = operateClientConfigurationProperties.getKeycloakUrl();
            keycloakRealm = operateClientConfigurationProperties.getKeycloakRealm();
            keycloakTokenUrl = operateClientConfigurationProperties.getKeycloakTokenUrl();
          } else if (hasText(operateClientConfigurationProperties.getUsername()) && hasText(operateClientConfigurationProperties.getPassword())) {
            selfManagedAuthenticationModeMap.put(OPERATE, SIMPLE);
          }
        }

        if (tasklistClientConfigurationProperties != null) {
          if (hasText(tasklistClientConfigurationProperties.getKeycloakUrl()) || hasText(tasklistClientConfigurationProperties.getKeycloakTokenUrl())) {
            selfManagedAuthenticationModeMap.put(TASKLIST, JWT);
            keycloakUrl = tasklistClientConfigurationProperties.getKeycloakUrl();
            keycloakRealm = tasklistClientConfigurationProperties.getKeycloakRealm();
            keycloakTokenUrl = tasklistClientConfigurationProperties.getKeycloakTokenUrl();
          } else if (hasText(tasklistClientConfigurationProperties.getUsername()) && hasText(tasklistClientConfigurationProperties.getPassword())) {
            selfManagedAuthenticationModeMap.put(TASKLIST, SIMPLE);
          }
        }

        if (commonConfigurationProperties != null) {
          if (hasText(commonConfigurationProperties.getKeycloak().getUrl()) || hasText(commonConfigurationProperties.getKeycloak().getTokenUrl())) {
            selfManagedAuthenticationModeMap.put(OPERATE, JWT);
            selfManagedAuthenticationModeMap.put(TASKLIST, JWT);
            keycloakUrl = commonConfigurationProperties.getKeycloak().getUrl();
            keycloakRealm = commonConfigurationProperties.getKeycloak().getRealm();
            keycloakTokenUrl = commonConfigurationProperties.getKeycloak().getTokenUrl();
          } else if (hasText(commonConfigurationProperties.getUsername()) && hasText(commonConfigurationProperties.getPassword())) {
            selfManagedAuthenticationModeMap.put(OPERATE, SIMPLE);
            selfManagedAuthenticationModeMap.put(TASKLIST, SIMPLE);
          }
        }

        // validate user properties
        SelfManagedAuthenticationMode mode = findSelfManagedAuthenticationMode(selfManagedAuthenticationModeMap);
        Authentication authentication = null;
        switch (mode) {
          case JWT:
            authentication = SelfManagedAuthentication.builder()
              .jwtConfig(configureJwtConfig())
              .keycloakUrl(keycloakUrl)
              .keycloakRealm(keycloakRealm)
              .keycloakTokenUrl(keycloakTokenUrl)
              .build();
            break;
          case SIMPLE:
            authentication = SimpleAuthentication.builder()
              .simpleConfig(configureSimpleConfig())
              .build();
            break;
        }
        return authentication;
      }
    }
    return new DefaultNoopAuthentication().build();
  }

  private SelfManagedAuthenticationMode findSelfManagedAuthenticationMode(Map<Product, SelfManagedAuthenticationMode> map) {
    HashSet<SelfManagedAuthenticationMode> uniqueValues = new HashSet<>(map.values());
    if (uniqueValues.size() == 1) {
      List<SelfManagedAuthenticationMode> list = new ArrayList<>(uniqueValues);
      return list.get(0);
    } else {
      throw new SdkException("Not allowed to mix JWT and Simple Authentication");
    }
  }

  private JwtConfig configureJwtConfig() {
    JwtConfig jwtConfig = new JwtConfig();
    // ZEEBE
    if (zeebeClientConfigurationProperties.getCloud().getClientId() != null && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
      jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(
        zeebeClientConfigurationProperties.getCloud().getClientId(),
        zeebeClientConfigurationProperties.getCloud().getClientSecret(),
        zeebeClientConfigurationProperties.getCloud().getAudience(),
        zeebeClientConfigurationProperties.getCloud().getAuthUrl())
      );
    } else if (zeebeSelfManagedProperties.getClientId() != null && zeebeSelfManagedProperties.getClientSecret() != null) {
      jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(
        zeebeSelfManagedProperties.getClientId(),
        zeebeSelfManagedProperties.getClientSecret(),
        zeebeSelfManagedProperties.getAudience(),
        zeebeSelfManagedProperties.getAuthServer())
      );
    } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
      jwtConfig.addProduct(Product.ZEEBE, new JwtCredential(
        commonConfigurationProperties.getClientId(),
        commonConfigurationProperties.getClientSecret(),
        zeebeClientConfigurationProperties.getCloud().getAudience(),
        zeebeClientConfigurationProperties.getCloud().getAuthUrl())
      );
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
      if (operateClientConfigurationProperties.getClientId() != null && operateClientConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(OPERATE, new JwtCredential(operateClientConfigurationProperties.getClientId(), operateClientConfigurationProperties.getClientSecret(), operateAudience, operateAuthUrl));
      } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(OPERATE, new JwtCredential(
          commonConfigurationProperties.getClientId(),
          commonConfigurationProperties.getClientSecret(),
          operateAudience,
          operateAuthUrl)
        );
      } else if (zeebeClientConfigurationProperties.getCloud().getClientId() != null && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
        jwtConfig.addProduct(OPERATE, new JwtCredential(zeebeClientConfigurationProperties.getCloud().getClientId(), zeebeClientConfigurationProperties.getCloud().getClientSecret(), operateAudience, operateAuthUrl));
      } else if (zeebeSelfManagedProperties.getClientId() != null && zeebeSelfManagedProperties.getClientSecret() != null) {
        jwtConfig.addProduct(OPERATE, new JwtCredential(zeebeSelfManagedProperties.getClientId(), zeebeSelfManagedProperties.getClientSecret(), operateAudience, operateAuthUrl));
      } else {
        throw new SdkException("Unable to determine OPERATE credentials");
      }
    }

    // TASKLIST
    String taskListAuthUrl = zeebeClientConfigurationProperties.getCloud().getAuthUrl();
    String taskListAudience = "tasklist.camunda.io";
    if (tasklistClientConfigurationProperties != null) {
      if (tasklistClientConfigurationProperties.getAuthUrl() != null) {
        taskListAuthUrl = tasklistClientConfigurationProperties.getAuthUrl();
      }
      if (tasklistClientConfigurationProperties.getBaseUrl() != null) {
        taskListAudience = tasklistClientConfigurationProperties.getBaseUrl();
      }
      if (tasklistClientConfigurationProperties.getClientId() != null && tasklistClientConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(TASKLIST, new JwtCredential(tasklistClientConfigurationProperties.getClientId(), tasklistClientConfigurationProperties.getClientSecret(), taskListAudience, taskListAuthUrl));
      } else if (commonConfigurationProperties.getClientId() != null && commonConfigurationProperties.getClientSecret() != null) {
        jwtConfig.addProduct(TASKLIST, new JwtCredential(
          commonConfigurationProperties.getClientId(),
          commonConfigurationProperties.getClientSecret(),
          operateAudience,
          operateAuthUrl)
        );
      } else if (zeebeClientConfigurationProperties.getCloud().getClientId() != null && zeebeClientConfigurationProperties.getCloud().getClientSecret() != null) {
        jwtConfig.addProduct(TASKLIST, new JwtCredential(zeebeClientConfigurationProperties.getCloud().getClientId(), zeebeClientConfigurationProperties.getCloud().getClientSecret(), taskListAudience, taskListAuthUrl));
      } else if (zeebeSelfManagedProperties.getClientId() != null && zeebeSelfManagedProperties.getClientSecret() != null) {
        jwtConfig.addProduct(TASKLIST, new JwtCredential(zeebeSelfManagedProperties.getClientId(), zeebeSelfManagedProperties.getClientSecret(), taskListAudience, taskListAuthUrl));
      } else {
        throw new SdkException("Unable to determine TASKLIST credentials");
      }
    }
    return jwtConfig;
  }

  private SimpleConfig configureSimpleConfig() {
    SimpleConfig simpleConfig = new SimpleConfig();
    // OPERATE
    if (operateClientConfigurationProperties != null) {
      if (hasText(operateClientConfigurationProperties.getUsername()) && hasText(operateClientConfigurationProperties.getPassword())) {
        simpleConfig.addProduct(OPERATE, new SimpleCredential(operateClientConfigurationProperties.getUsername(), operateClientConfigurationProperties.getPassword(), operateClientConfigurationProperties.getAuthUrl()));
      } else if (hasText(commonConfigurationProperties.getUsername()) && hasText(commonConfigurationProperties.getPassword())) {
        simpleConfig.addProduct(OPERATE, new SimpleCredential(commonConfigurationProperties.getUsername(), commonConfigurationProperties.getPassword(), commonConfigurationProperties.getAuthUrl()));
      }
    }

    // TASKLIST
    if (tasklistClientConfigurationProperties != null) {
      if (hasText(tasklistClientConfigurationProperties.getUsername()) && hasText(tasklistClientConfigurationProperties.getPassword())) {
        simpleConfig.addProduct(OPERATE, new SimpleCredential(tasklistClientConfigurationProperties.getUsername(), tasklistClientConfigurationProperties.getPassword(), tasklistClientConfigurationProperties.getAuthUrl()));
      } else if (hasText(commonConfigurationProperties.getUsername()) && hasText(commonConfigurationProperties.getPassword())) {
        simpleConfig.addProduct(OPERATE, new SimpleCredential(commonConfigurationProperties.getUsername(), commonConfigurationProperties.getPassword(), commonConfigurationProperties.getAuthUrl()));
      }
    }
    return simpleConfig;
  }
}
