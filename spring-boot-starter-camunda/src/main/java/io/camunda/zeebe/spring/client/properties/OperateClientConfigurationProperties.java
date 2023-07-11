package io.camunda.zeebe.spring.client.properties;

import io.camunda.operate.auth.AuthInterface;
import io.camunda.operate.auth.SaasAuthentication;
import io.camunda.operate.auth.SelfManagedAuthentication;
import io.camunda.operate.auth.SimpleAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.invoke.MethodHandles;

@ConfigurationProperties
public class OperateClientConfigurationProperties {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    // Normal Zeebe Engine Properties
    @Value("${zeebe.client.cloud.cluster-id:#{null}}")
    private String clusterId;

    @Value("${zeebe.client.cloud.region:bru-2}")
    private String region;

    @Value("${zeebe.client.cloud.client-id:#{null}}")
    private String clientId;

    @Value("${zeebe.client.cloud.client-secret:#{null}}")
    private String clientSecret;

    @Value("${zeebe.client.cloud.authUrl:#{null}}")
    private String authUrlZeebe;

    @Value("${zeebe.client.cloud.baseUrl:#{null}}")
    private String audienceZeebe;

    public static String DEFAULT_AUTH_URL = "https://login.cloud.camunda.io/oauth/token";
    public static String DEFAULT_AUDIENCE = "operate.camunda.io";

    @Value("${camunda.operate.client.authUrl:#{null}}")
    private String authUrlOperate;

    @Value("${camunda.operate.client.baseUrl:#{null}}")
    private String audienceOperate;

    // Specific properties to overwrite for Operate
    @Value("${camunda.operate.client.client-id:#{null}}")
    private String operateClientId;

    @Value("${camunda.operate.client.client-secret:#{null}}")
    private String operateClientSecret;

    @Value("${camunda.operate.client.url:#{null}}")
    private String operateUrl;

    @Value("${camunda.operate.client.username:#{null}}")
    private String operateUsername;

    @Value("${camunda.operate.client.password:#{null}}")
    private String operatePassword;

    @Value("${camunda.operate.client.keycloak-token-url:#{null}}")
    private String operateKeycloakTokenUrl;
  
    @Value("${camunda.operate.client.keycloak-url:#{null}}")
    private String operateKeycloakUrl;

    @Value("${camunda.operate.client.keycloak-realm:#{null}}")
    private String operateKeycloakRealm;

    // TODO: This currently assumes PROD in Cloud - do we want to support DEV and INT?
    // and make it configurable? At the moment the workaround is to set the operateUrl yourself
    public static String operateCloudBaseUrl = "operate.camunda.io";

    public String getOperateUrl() {
      if (operateUrl != null) {
        LOG.debug("Connecting to Camunda Operate on URL: " + operateUrl);
        return operateUrl;
      } else if (clusterId != null) {
        String url = "https://" + region + "." + operateCloudBaseUrl + "/" + clusterId + "/";
        LOG.debug("Connecting to Camunda Operate SaaS via URL: " + url);
        return url;
      }
      throw new IllegalArgumentException(
        "In order to connect to Camunda Operate you need to specify either a SaaS clusterId or an Operate URL.");
    }

    public AuthInterface getAuthentication(String operateUrl) {
      String clientId = operateClientId != null ? operateClientId : this.clientId;
      String clientSecret = operateClientSecret != null ? operateClientSecret : this.clientSecret;

      if (operateKeycloakUrl != null || operateKeycloakTokenUrl!=null) {
        if (clientId!=null && clientSecret !=null) {
          SelfManagedAuthentication authentication = new SelfManagedAuthentication(clientId, clientSecret);
          if (operateKeycloakUrl!=null) {
            LOG.debug("Authenticating with Camunda Operate using Keycloak on " + operateKeycloakUrl);
            return authentication.keycloakUrl(operateKeycloakUrl)
              .keycloakRealm(operateKeycloakRealm);
          } else {
            LOG.debug("Authenticating with Camunda Operate using Keycloak token url " + operateKeycloakTokenUrl);
            return authentication.keycloakTokenUrl(operateKeycloakTokenUrl);
          }
        }
        
        throw new IllegalArgumentException(
          "Failed to authenticate with Camunda Operate using Keycloak: "
            + "please configure client ID and client secret values.");
      } else {
        if (clientId != null) {
          LOG.debug("Authenticating with Camunda Operate using client id and secret");
          return new SaasAuthentication(getAuthUrl(), getAudience(), clientId, clientSecret);
        } else if (operateUsername != null && operatePassword != null) {
          LOG.debug("Authenticating with Camunda Operate using username and password");
          return new SimpleAuthentication(operateUsername, operatePassword, operateUrl);
        }
      }
      throw new IllegalArgumentException(
        "In order to connect to Camunda Operate you need to configure authentication properly. "
          + "You can use password-based authentication, or authenticate with Keycloak. "
          + "Please configure either one of the methods.");
    }

    public String getAuthUrl() {
      if (authUrlOperate!=null) {
        return authUrlOperate;
      } else if (authUrlZeebe!=null) {
        return authUrlZeebe;
      } else {
        return DEFAULT_AUTH_URL;
      }
    }

    public String getAudience() {
      if (audienceOperate!=null) {
        return audienceOperate;
      } else if (audienceZeebe!=null) {
        return audienceZeebe;
      } else {
        return DEFAULT_AUDIENCE;
      }
    }

}
