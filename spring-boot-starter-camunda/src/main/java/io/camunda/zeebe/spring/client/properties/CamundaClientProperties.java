package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.ZeebeClientProperties;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("camunda.client")
public class CamundaClientProperties {

  private ClientMode mode;
  private String clusterId;
  private String region;
  @NestedConfigurationProperty private List<String> tenantIds;
  @NestedConfigurationProperty private AuthProperties auth;

  @NestedConfigurationProperty private ApiProperties operate;
  @NestedConfigurationProperty private ApiProperties tasklist;
  @NestedConfigurationProperty private ApiProperties optimize;
  @NestedConfigurationProperty private ApiProperties identity;
  @NestedConfigurationProperty private ZeebeClientProperties zeebe;

  public ClientMode getMode() {
    return mode;
  }

  public void setMode(ClientMode mode) {
    this.mode = mode;
  }

  public AuthProperties getAuth() {
    return auth;
  }

  public void setAuth(AuthProperties auth) {
    this.auth = auth;
  }

  public ApiProperties getOperate() {
    return operate;
  }

  public void setOperate(ApiProperties operate) {
    this.operate = operate;
  }

  public ApiProperties getTasklist() {
    return tasklist;
  }

  public void setTasklist(ApiProperties tasklist) {
    this.tasklist = tasklist;
  }

  public ApiProperties getOptimize() {
    return optimize;
  }

  public void setOptimize(ApiProperties optimize) {
    this.optimize = optimize;
  }

  public ZeebeClientProperties getZeebe() {
    return zeebe;
  }

  public void setZeebe(ZeebeClientProperties zeebe) {
    this.zeebe = zeebe;
  }

  public ApiProperties getIdentity() {
    return identity;
  }

  public void setIdentity(ApiProperties identity) {
    this.identity = identity;
  }

  public List<String> getTenantIds() {
    return tenantIds;
  }

  public void setTenantIds(List<String> tenantIds) {
    this.tenantIds = tenantIds;
  }

  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public enum ClientMode {
    simple,
    oidc,
    saas
  }
}
