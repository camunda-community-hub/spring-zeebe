package io.camunda.zeebe.spring.client.properties;

import io.camunda.commons.auth.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class CommonClientConfigurationProperties {

  @Value("${zeebe.client.cloud.cluster-id:#{null}}")
  private String clusterId;

  //@Value("${zeebe.client.cloud.region:bru-2}")
  //private String region;

  @Value("${zeebe.client.cloud.client-id:#{null}}")
  private String zeebeClientId;

  @Value("${zeebe.client.cloud.client-secret:#{null}}")
  private String zeebeClientSecret;

  @Value("${zeebe.client.enabled:#{true}}")
  private Boolean zeebeEnabled;

  @Value("${zeebe.client.cloud.authUrl:#{null}}")
  private String authUrlZeebe;

  @Value("${zeebe.client.cloud.baseUrl:#{null}}")
  private String audienceZeebe;

  // TODO: refactor to its own client props maybe?
  // operate - check why zeebe has different casing with dash '-'
  @Value("${camunda.operate.client.clientId:#{null}}")
  private String operateClientId;

  @Value("${camunda.operate.client.clientSecret:#{null}}")
  private String operateClientSecret;

  @Value("${camunda.operate.client.enabled:#{true}}")
  private Boolean operateEnabled;

  // console
  @Value("${camunda.console.client.clientId:#{null}}")
  private String consoleClientId;

  @Value("${camunda.console.client.clientSecret:#{null}}")
  private String consoleClientSecret;

  @Value("${camunda.console.client.enabled:#{true}}")
  private Boolean consoleEnabled;

  // optimize
  @Value("${camunda.optimize.client.clientId:#{null}}")
  private String optimizeClientId;

  @Value("${camunda.optimize.client.clientSecret:#{null}}")
  private String optimizeClientSecret;

  @Value("${camunda.optimize.client.enabled:#{true}}")
  private Boolean optimizeEnabled;

  // tasklist
  @Value("${camunda.tasklist.client.clientId:#{null}}")
  private String tasklistClientId;

  @Value("${camunda.tasklist.client.clientSecret:#{null}}")
  private String tasklistClientSecret;

  @Value("${camunda.tasklist.client.enabled:#{true}}")
  private Boolean tasklistEnabled;

  @Value("${camunda.keycloak.url:#{null}}")
  private String keycloakUrl;

  @Value("${camunda.keycloak.realm:#{null}}")
  private String keycloakRealm;

  @Value("${camunda.client.clientId:#{null}}")
  private String commonClientId;

  @Value("${camunda.client.clientSecret:#{null}}")
  private String commonClientSecret;

  public Authentication getAuthentication() {

    // check for at-least one existence, then its JWT based authentication
    if ((zeebeClientId != null ) || (operateClientId != null) || (consoleClientId != null) || (optimizeClientId != null) || (tasklistClientId != null)) {
      JwtConfig jwtConfig = new JwtConfig();

      if (zeebeEnabled) {
        if (zeebeClientId != null && zeebeClientSecret != null) {
          jwtConfig.addProduct(Product.ZEEBE, new Credential(zeebeClientId, zeebeClientSecret));
        } else if (commonClientId != null && commonClientSecret != null) {
          jwtConfig.addProduct(Product.ZEEBE, new Credential(commonClientId, commonClientSecret));
        }
      }

      if (operateEnabled) {
        if (operateClientId != null && operateClientSecret != null) {
          jwtConfig.addProduct(Product.OPERATE, new Credential(operateClientId, operateClientSecret));
        } else if (commonClientId != null && commonClientSecret != null) {
          jwtConfig.addProduct(Product.OPERATE, new Credential(commonClientId, commonClientSecret));
        }
      }

      if (consoleEnabled) {
        if (consoleClientId != null && consoleClientSecret != null) {
          jwtConfig.addProduct(Product.CONSOLE, new Credential(consoleClientId, consoleClientSecret));
        } else if (commonClientId != null && commonClientSecret != null) {
          jwtConfig.addProduct(Product.CONSOLE, new Credential(commonClientId, commonClientSecret));
        }
      }

      if (optimizeEnabled) {
        if (optimizeClientId != null && optimizeClientSecret != null) {
          jwtConfig.addProduct(Product.OPTIMIZE, new Credential(optimizeClientId, optimizeClientSecret));
        } else if (commonClientId != null && commonClientSecret != null) {
          jwtConfig.addProduct(Product.OPTIMIZE, new Credential(commonClientId, commonClientSecret));
        }
      }

      if (tasklistEnabled) {
        if (tasklistClientId != null && tasklistClientSecret != null) {
          jwtConfig.addProduct(Product.TASKLIST, new Credential(tasklistClientId, tasklistClientSecret));
        } else if (commonClientId != null && commonClientSecret != null) {
          jwtConfig.addProduct(Product.TASKLIST, new Credential(commonClientId, commonClientSecret));
        }
      }

      if (keycloakUrl != null) {
        return new SelfManagedAuthentication()
          .jwtConfig(jwtConfig)
          .keycloakUrl(keycloakUrl)
          .keycloakRealm(keycloakRealm)
          .build();
      } else {
        return new SaaSAuthentication()
          .jwtConfig(jwtConfig)
          .build();
      }
    } else {
      // TODO: SimpleAuthentication and other authentication
      throw new UnsupportedOperationException("not implemented");
    }
  }
}
