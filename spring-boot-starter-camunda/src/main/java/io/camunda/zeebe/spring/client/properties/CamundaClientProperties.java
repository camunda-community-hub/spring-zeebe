package io.camunda.zeebe.spring.client.properties;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.camunda.zeebe.spring.client.bean.CopyNotNullBeanUtilsBean;
import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties;
import io.camunda.zeebe.spring.client.properties.common.ZeebeGatewayProperties;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static java.util.Optional.*;

@ConfigurationProperties("camunda.client")
public class CamundaClientProperties {

  public static final Map<ClientMode, CamundaClientProperties> DEFAULT_CLIENT_PROPERTIES;
  private static final CopyNotNullBeanUtilsBean BEAN_UTILS_BEAN = new CopyNotNullBeanUtilsBean();

  static {
    DEFAULT_CLIENT_PROPERTIES = new HashMap<>();
    DEFAULT_CLIENT_PROPERTIES.put(ClientMode.simple, simpleClientProperties());
    DEFAULT_CLIENT_PROPERTIES.put(ClientMode.saas, saasClientProperties());
    DEFAULT_CLIENT_PROPERTIES.put(ClientMode.oidc, oidcClientProperties());
  }

  private ClientMode mode;
  private String clusterId;
  private String region;
  private List<String> tenantIds;
  private GlobalAuthProperties auth;
  private ApiProperties operate;
  private ApiProperties tasklist;
  private ApiProperties optimize;
  private ApiProperties console;
  private ApiProperties webModeler;
  private ApiProperties identity;
  private ZeebeGatewayProperties zeebe;

  private static CamundaClientProperties simpleClientProperties() {
    return fromResource("default-properties/simple.yaml");
  }

  private static CamundaClientProperties saasClientProperties() {
    return fromResource("default-properties/saas.yaml");
  }

  private static CamundaClientProperties oidcClientProperties() {
    return fromResource("default-properties/oidc.yaml");
  }

  private static CamundaClientProperties fromResource(String resource) {
    YAMLMapper mapper = new YAMLMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    try (InputStream in =
        CamundaClientProperties.class.getClassLoader().getResourceAsStream(resource)) {
      return mapper.treeToValue(
          mapper.readTree(in).get("camunda").get("client"), CamundaClientProperties.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Error while loading camunda client properties from resource " + resource, e);
    }
  }

  @PostConstruct
  public void init() {
    detectMode();
    applyDefaults();
    projectProperties();
    if(mode == ClientMode.saas){
      applySaasDefaults();
    }
  }

  private void detectMode() {
    if (this.mode == null) {
      if (StringUtils.hasText(clusterId)) {
        this.mode = ClientMode.saas;
      } else if (hasOidcCredentialSet()) {
        this.mode = ClientMode.oidc;
      } else if (hasSimpleCredentialSet()) {
        this.mode = ClientMode.simple;
      } else {
        throw new IllegalStateException("Could not detect camunda client mode");
      }
    }
  }

  private void applyDefaults() {
    try {
      CamundaClientProperties defaults =
          (CamundaClientProperties) BeanUtils.cloneBean(DEFAULT_CLIENT_PROPERTIES.get(mode));
      // copy actual properties to the defaults
      BEAN_UTILS_BEAN.copyProperties(defaults, this);
      // copy remaining defaults to the actual properties
      BEAN_UTILS_BEAN.copyProperties(this, defaults);
    } catch (IllegalAccessException
        | InvocationTargetException
        | InstantiationException
        | NoSuchMethodException e) {
      throw new RuntimeException("Error while applying defaults to camunda client properties", e);
    }
  }

  private void projectProperties() {
    allPresentApiProperties()
        .filter(p -> !GlobalAuthProperties.class.isAssignableFrom(p.getClass()))
        .forEach(
            authProperties -> {
              try {
                GlobalAuthProperties globalDefaults =
                    (GlobalAuthProperties) BeanUtils.cloneBean(auth);
                // copy actual properties to the defaults
                BEAN_UTILS_BEAN.copyProperties(globalDefaults, authProperties);
                // copy remaining defaults to the actual properties
                BEAN_UTILS_BEAN.copyProperties(authProperties, globalDefaults);
              } catch (IllegalAccessException
                  | InstantiationException
                  | InvocationTargetException
                  | NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private void applySaasDefaults(){
    Assert.hasLength(region,"Please set camunda.client.region");
    Assert.hasLength(clusterId,"Please set camunda.client.cluster-id");
    String zeebeBaseUrl = String.format("https://%s.%s.zeebe.camunda.io",clusterId,region);
    String operateBaseUrl = String.format("https://%s.operate.camunda.io/%s",region,clusterId);
    String tasklistBaseUrl = String.format("https://%s.tasklist.camunda.io/%s",region,clusterId);
    String optimizeBaseUrl = String.format("https://%s.optimize.camunda.io/%s",region,clusterId);
    zeebe.setBaseUrl(zeebeBaseUrl);
    operate.setBaseUrl(operateBaseUrl);
    tasklist.setBaseUrl(tasklistBaseUrl);
    optimize.setBaseUrl(optimizeBaseUrl);
  }

  private boolean hasOidcCredentialSet() {
    return allPresentApiProperties()
        .anyMatch(
            authProperties ->
                authProperties.getClientId() != null && authProperties.getClientSecret() != null);
  }

  private Stream<AuthProperties> allPresentApiProperties() {
    return Stream.of(auth, zeebe, operate, tasklist, optimize, webModeler, console, identity)
        .filter(Objects::nonNull);
  }

  private boolean hasSimpleCredentialSet() {
    return allPresentApiProperties()
        .anyMatch(
            authProperties ->
                authProperties.getUsername() != null && authProperties.getPassword() != null);
  }

  public ClientMode getMode() {
    return mode;
  }

  public void setMode(ClientMode mode) {
    this.mode = mode;
  }

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
