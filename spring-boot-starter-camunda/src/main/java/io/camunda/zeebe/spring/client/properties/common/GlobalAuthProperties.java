package io.camunda.zeebe.spring.client.properties.common;

import io.camunda.identity.sdk.IdentityConfiguration;
import io.camunda.identity.sdk.IdentityConfiguration.Type;

public class GlobalAuthProperties extends AuthProperties {
  private IdentityConfiguration.Type oidcType;
  private String issuer;
  private String issuerBackendUrl;

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
}
