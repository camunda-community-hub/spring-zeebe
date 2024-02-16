package io.camunda.zeebe.spring.client.properties;

import io.camunda.identity.sdk.IdentityConfiguration.Type;
import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties.AuthMode;
import io.camunda.zeebe.spring.client.properties.common.ZeebeGatewayProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("camunda.client")
public class CamundaClientProperties {

  public static final Map<AuthMode, CamundaClientProperties> DEFAULT_CLIENT_PROPERTIES;

  static {
    DEFAULT_CLIENT_PROPERTIES = new HashMap<>();
    // TODO: create meaningful defaults
    // simple: username demo, password demo, localhost base urls -> should word with docker compose
    // just by setting camunda.client.auth.mode: simple
    DEFAULT_CLIENT_PROPERTIES.put(AuthMode.simple,simpleClientProperties());
    // saas: saas issuer url, region -> should work by setting camunda.client.auth.cluster-id/client-id/client-secret/region
    DEFAULT_CLIENT_PROPERTIES.put(AuthMode.saas,saasClientProperties());
    // oidc: localhost base urls -> should work with docker compose or port-forwarded helm chart
    DEFAULT_CLIENT_PROPERTIES.put(AuthMode.oidc,oidcClientProperties());
  }

  private static CamundaClientProperties simpleClientProperties(){
    CamundaClientProperties simpleProperties = new CamundaClientProperties();
    // global: credentials
    GlobalAuthProperties globalAuthProperties = new GlobalAuthProperties();
    globalAuthProperties.setUsername("demo");
    globalAuthProperties.setPassword("demo");
    simpleProperties.setAuth(globalAuthProperties);
    // zeebe
    ZeebeGatewayProperties zeebeGatewayProperties = new ZeebeGatewayProperties();
    zeebeGatewayProperties.setEnabled(true);
    zeebeGatewayProperties.setBaseUrl("localhost:26500");
    zeebeGatewayProperties.setPlaintext(true);
    simpleProperties.setZeebe(zeebeGatewayProperties);
    // operate
    ApiProperties operateProperties = ApiProperties.enabled();
    operateProperties.setBaseUrl("http://localhost:8081");
    simpleProperties.setOperate(operateProperties);
    // tasklist
    ApiProperties tasklistProperties = ApiProperties.enabled();
    tasklistProperties.setBaseUrl("http://localhost:8082");
    simpleProperties.setTasklist(tasklistProperties);
    // disable all others
    simpleProperties.setOptimize(ApiProperties.disabled());
    simpleProperties.setWebModeler(ApiProperties.disabled());
    simpleProperties.setConsole(ApiProperties.disabled());
    simpleProperties.setIdentity(ApiProperties.disabled());
    return simpleProperties;
  }
  private static CamundaClientProperties saasClientProperties(){
    CamundaClientProperties properties = new CamundaClientProperties();
    // all applications enabled except console and web modeler
    ZeebeGatewayProperties zeebeGatewayProperties = new ZeebeGatewayProperties();
    zeebeGatewayProperties.setEnabled(true);
    properties.setZeebe(zeebeGatewayProperties);
    // TODO add default audiences
    properties.setOperate(ApiProperties.enabled());
    properties.setTasklist(ApiProperties.enabled());
    properties.setOptimize(ApiProperties.enabled());
    properties.setConsole(ApiProperties.disabled());
    properties.setWebModeler(ApiProperties.disabled());
    properties.setIdentity(ApiProperties.disabled());
    return properties;
  }

  private static CamundaClientProperties oidcClientProperties(){
    CamundaClientProperties properties = new CamundaClientProperties();
    GlobalAuthProperties globalAuthProperties = new GlobalAuthProperties();
    properties.setAuth(globalAuthProperties);
    globalAuthProperties.setOidcType(Type.KEYCLOAK);
    globalAuthProperties.setIssuer("http://localhost:18080/auth/realms/camunda-platform");
    globalAuthProperties.setIssuerBackendUrl("http://keycloak:8080/auth/realms/camunda-platform");
    // zeebe
    ZeebeGatewayProperties zeebeGatewayProperties = new ZeebeGatewayProperties();
    zeebeGatewayProperties.setEnabled(true);
    zeebeGatewayProperties.setBaseUrl("localhost:26500");
    zeebeGatewayProperties.setPlaintext(true);
    properties.setZeebe(zeebeGatewayProperties);
    // operate
    ApiProperties operateProperties = ApiProperties.enabled();
    operateProperties.setBaseUrl("http://localhost:8081");
    properties.setOperate(operateProperties);
    // tasklist
    ApiProperties tasklistProperties = ApiProperties.enabled();
    tasklistProperties.setBaseUrl("http://localhost:8082");
    properties.setTasklist(tasklistProperties);
    // optimize
    ApiProperties optimizeProperties = ApiProperties.enabled();
    optimizeProperties.setBaseUrl("http://localhost:8083");
    properties.setOptimize(optimizeProperties);
    // identity
    ApiProperties identity = ApiProperties.enabled();
    identity.setBaseUrl("http://localhost:8084");
    properties.setIdentity(identity);
    // disable all others
    properties.setWebModeler(ApiProperties.disabled());
    properties.setConsole(ApiProperties.disabled());
    return properties;
  }
  private List<String> tenantIds;

  private GlobalAuthProperties auth;
  private ApiProperties operate;
  private ApiProperties tasklist;
  private ApiProperties optimize;
  private ApiProperties console;
  private ApiProperties webModeler;
  private ApiProperties identity;
  private ZeebeGatewayProperties zeebe;

  public GlobalAuthProperties getAuth() {
    return auth;
  }

  public void setAuth(GlobalAuthProperties auth) {
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

  public ApiProperties getConsole() {
    return console;
  }

  public void setConsole(ApiProperties console) {
    this.console = console;
  }

  public ApiProperties getWebModeler() {
    return webModeler;
  }

  public void setWebModeler(ApiProperties webModeler) {
    this.webModeler = webModeler;
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
}
