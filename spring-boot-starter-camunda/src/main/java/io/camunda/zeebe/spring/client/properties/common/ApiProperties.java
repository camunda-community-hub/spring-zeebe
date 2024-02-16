package io.camunda.zeebe.spring.client.properties.common;

import java.util.List;

public class ApiProperties extends AuthProperties {
  private Boolean enabled;
  private String baseUrl;
  private List<String> tenantIds;

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<String> getTenantIds() {
    return tenantIds;
  }

  public void setTenantIds(List<String> tenantIds) {
    this.tenantIds = tenantIds;
  }
}
