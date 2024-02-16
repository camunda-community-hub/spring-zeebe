package io.camunda.zeebe.spring.client.properties.common;

import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties.AuthMode;

import java.util.HashMap;
import java.util.Map;

public class AuthProperties {


  // simple
  private String username;
  private String password;
  // oidc
  // oidc and saas
  private String clientId;
  private String clientSecret;
  // saas
  private String clusterId;



  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }



  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }


}
