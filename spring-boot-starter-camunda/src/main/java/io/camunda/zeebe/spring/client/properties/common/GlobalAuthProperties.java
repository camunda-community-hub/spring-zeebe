package io.camunda.zeebe.spring.client.properties.common;

import io.camunda.identity.sdk.IdentityConfiguration;
import io.camunda.identity.sdk.IdentityConfiguration.Type;

import java.util.List;

public class GlobalAuthProperties extends AuthProperties {
  private AuthMode mode;
  private IdentityConfiguration.Type oidcType;
  private String issuer;
  private String issuerBackendUrl;
  private String region;


  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public AuthMode getMode() {
    return mode;
  }

  public void setMode(AuthMode mode) {
    this.mode = mode;
  }

  public Type getOidcType() {
    return oidcType;
  }

  public void setOidcType(Type oidcType) {
    this.oidcType = oidcType;
  }
  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getIssuerBackendUrl() {
    return issuerBackendUrl;
  }

  public void setIssuerBackendUrl(String issuerBackendUrl) {
    this.issuerBackendUrl = issuerBackendUrl;
  }



  public enum AuthMode {
    simple, oidc, saas
  }
}
