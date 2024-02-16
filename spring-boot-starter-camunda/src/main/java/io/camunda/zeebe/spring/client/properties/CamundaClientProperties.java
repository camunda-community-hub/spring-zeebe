package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.ZeebeGatewayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("camunda.client")
public class CamundaClientProperties {
  private AuthProperties auth;
  private ApiProperties operate;
  private ApiProperties tasklist;
  private ApiProperties optimize;
  private ZeebeGatewayProperties zeebe;

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

  public ZeebeGatewayProperties getZeebe() {
    return zeebe;
  }

  public void setZeebe(ZeebeGatewayProperties zeebe) {
    this.zeebe = zeebe;
  }
}
