package io.camunda.zeebe.spring.client.properties;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;

import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties.ClientMode;
import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties;
import io.grpc.ClientInterceptor;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaClientPropertiesTest {

  @Test
  void shouldLoadDefaults_simple(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.simple);
    properties.init();
    assertThat(properties.getMode()).isEqualTo(ClientMode.simple);
    assertThat(properties.getAuth().getUsername()).isEqualTo("demo");
    assertThat(properties.getAuth().getPassword()).isEqualTo("demo");
    assertThat(properties.getZeebe().getBaseUrl()).isEqualTo("http://localhost:26500");
    assertThat(properties.getZeebe().getEnabled()).isEqualTo(true);
    assertThat(properties.getOperate().getBaseUrl()).isEqualTo("http://localhost:8081");
    assertThat(properties.getOperate().getEnabled()).isEqualTo(true);
    assertThat(properties.getTasklist().getBaseUrl()).isEqualTo("http://localhost:8082");
    assertThat(properties.getTasklist().getEnabled()).isEqualTo(true);
    assertThat(properties.getWebModeler().getEnabled()).isEqualTo(false);
    assertThat(properties.getOptimize().getEnabled()).isEqualTo(false);
    assertThat(properties.getConsole().getEnabled()).isEqualTo(false);
  }

  @Test
  void shouldNotOverrideSetValues(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.simple);
    GlobalAuthProperties globalAuthProperties = new GlobalAuthProperties();
    globalAuthProperties.setUsername("demo2");
    properties.setAuth(globalAuthProperties);
    properties.init();
    assertThat(properties.getAuth().getUsername()).isEqualTo("demo2");
  }

  @Test
  void shouldReflectGlobalAuthProperties(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.saas);
    properties.setClusterId("my-cluster-id");
    properties.setRegion("bru-2");
    GlobalAuthProperties globalAuthProperties = new GlobalAuthProperties();
    globalAuthProperties.setClientId("my-client-for-everything");
    properties.setAuth(globalAuthProperties);
    properties.init();
    assertThat(properties.getTasklist().getClientId()).isEqualTo("my-client-for-everything");
  }

  @Test
  void shouldNotReflectGlobalAuthPropertiesToSetValues(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.saas);
    properties.setClusterId("my-cluster-id");
    properties.setRegion("bru-2");
    GlobalAuthProperties globalAuthProperties = new GlobalAuthProperties();
    globalAuthProperties.setClientId("my-client-for-everything");
    properties.setAuth(globalAuthProperties);
    ApiProperties operateProperties = new ApiProperties();
    operateProperties.setClientId("my-client-for-operate");
    properties.setOperate(operateProperties);
    properties.init();
    assertThat(properties.getTasklist().getClientId()).isEqualTo("my-client-for-everything");
    assertThat(properties.getOperate().getClientId()).isEqualTo("my-client-for-operate");
  }

  @Test
  void shouldPopulateBaseUrlsForSaas(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.saas);
    properties.setClusterId("my-cluster-id");
    properties.setRegion("bru-2");
    properties.init();
    assertThat(properties.getZeebe().getBaseUrl()).isEqualTo("https://my-cluster-id.bru-2.zeebe.camunda.io");
    assertThat(properties.getOperate().getBaseUrl()).isEqualTo("https://bru-2.operate.camunda.io/my-cluster-id");
    assertThat(properties.getTasklist().getBaseUrl()).isEqualTo("https://bru-2.tasklist.camunda.io/my-cluster-id");
    assertThat(properties.getOptimize().getBaseUrl()).isEqualTo("https://bru-2.optimize.camunda.io/my-cluster-id");

  }

  @Test
  void shouldLoadDefaults_saas(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.saas);
    properties.setClusterId("my-cluster-id");
    properties.setRegion("my-region");
    properties.init();
    assertThat(properties.getMode()).isEqualTo(ClientMode.saas);
    assertThat(properties.getAuth().getIssuer()).isEqualTo("https://login.cloud.camunda.io/oauth/token");
    assertThat(properties.getZeebe().getEnabled()).isEqualTo(true);
    assertThat(properties.getZeebe().getAudience()).isEqualTo("zeebe.camunda.io");
    assertThat(properties.getOperate().getEnabled()).isEqualTo(true);
    assertThat(properties.getOperate().getAudience()).isEqualTo("operate.camunda.io");
    assertThat(properties.getTasklist().getEnabled()).isEqualTo(true);
    assertThat(properties.getTasklist().getAudience()).isEqualTo("tasklist.camunda.io");
    assertThat(properties.getWebModeler().getEnabled()).isEqualTo(false);
    assertThat(properties.getWebModeler().getAudience()).isEqualTo("api.cloud.camunda.io");
    assertThat(properties.getWebModeler().getBaseUrl()).isEqualTo("https://modeler.cloud.camunda.io");
    assertThat(properties.getOptimize().getEnabled()).isEqualTo(true);
    assertThat(properties.getConsole().getEnabled()).isEqualTo(false);
    assertThat(properties.getConsole().getAudience()).isEqualTo("api.cloud.camunda.io");
    assertThat(properties.getConsole().getBaseUrl()).isEqualTo("https://console.cloud.camunda.io");
  }

  @Test
  void shouldLoadDefaults_oidc(){
    CamundaClientProperties properties = new CamundaClientProperties();
    properties.setMode(ClientMode.oidc);
    properties.init();
    assertThat(properties.getMode()).isEqualTo(ClientMode.oidc);
    assertThat(properties.getZeebe().getBaseUrl()).isEqualTo("http://localhost:26500");
    assertThat(properties.getZeebe().getEnabled()).isEqualTo(true);
    assertThat(properties.getOperate().getBaseUrl()).isEqualTo("http://localhost:8081");
    assertThat(properties.getOperate().getEnabled()).isEqualTo(true);
    assertThat(properties.getTasklist().getBaseUrl()).isEqualTo("http://localhost:8082");
    assertThat(properties.getTasklist().getEnabled()).isEqualTo(true);
    assertThat(properties.getWebModeler().getEnabled()).isEqualTo(false);
    assertThat(properties.getOptimize().getEnabled()).isEqualTo(true);
    assertThat(properties.getConsole().getEnabled()).isEqualTo(false);
  }

  private static class ZeebeClientConfig implements ZeebeClientConfiguration {
    public static final ZeebeClientBuilderImpl DEFAULT =
        (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());
    private static final Logger LOG = LoggerFactory.getLogger(ZeebeClientConfig.class);
    private final CamundaClientProperties properties;

    public ZeebeClientConfig(CamundaClientProperties properties) {
      this.properties = properties;
    }

    private <T> T getOrDefault(String propertyName, Optional<T> property, T defaultProperty) {
      return property.orElseGet(
          () -> {
            LOG.debug("{} not set, defaulting to '{}'", propertyName, defaultProperty);
            return defaultProperty;
          });
    }

    @Override
    public String getGatewayAddress() {
      return getOrDefault(
          "GatewayAddress",
          ofNullable(properties.getZeebe().getBaseUrl()).map(url -> URI.create(url).getHost()),
          DEFAULT.getGatewayAddress());
    }

    @Override
    public String getDefaultTenantId() {
      return getOrDefault(
          "DefaultTenantId",
          ofNullable(properties.getTenantIds())
              .filter(list -> !list.isEmpty())
              .map(list -> list.get(0)),
          DEFAULT.getDefaultTenantId());
    }

    @Override
    public List<String> getDefaultJobWorkerTenantIds() {
      return getOrDefault(
          "DefaultJobWorkerTenantIds",
          ofNullable(properties.getTenantIds()),
          DEFAULT.getDefaultJobWorkerTenantIds());
    }

    @Override
    public int getNumJobWorkerExecutionThreads() {
      return getOrDefault(
          "NumJobWorkerExecutionThreads",
          ofNullable(properties.getZeebe().getExecutionThreads()),
          DEFAULT.getNumJobWorkerExecutionThreads());
    }

    @Override
    public int getDefaultJobWorkerMaxJobsActive() {
      return getOrDefault(
          "DefaultJobWorkerMaxJobsActive",
          ofNullable(properties.getZeebe().getMaxJobsActive()),
          DEFAULT.getDefaultJobWorkerMaxJobsActive());
    }

    @Override
    public String getDefaultJobWorkerName() {
      return getOrDefault(
          "DefaultJobWorkerName",
          ofNullable(properties.getZeebe().getJobWorkerName()),
          DEFAULT.getDefaultJobWorkerName());
    }

    @Override
    public Duration getDefaultJobTimeout() {
      return getOrDefault(
          "DefaultJobTimeout",
          ofNullable(properties.getZeebe().getJobTimeout()),
          DEFAULT.getDefaultJobTimeout());
    }

    @Override
    public Duration getDefaultJobPollInterval() {
      return getOrDefault(
          "DefaultJobPollInterval",
          ofNullable(properties.getZeebe().getJobPollInterval()),
          DEFAULT.getDefaultJobPollInterval());
    }

    @Override
    public Duration getDefaultMessageTimeToLive() {
      return getOrDefault(
          "DefaultMessageTimeToLive",
          ofNullable(properties.getZeebe().getMessageTimeToLive()),
          DEFAULT.getDefaultMessageTimeToLive());
    }

    @Override
    public Duration getDefaultRequestTimeout() {
      return getOrDefault(
          "DefaultRequestTimeout",
          ofNullable(properties.getZeebe().getRequestTimeout()),
          DEFAULT.getDefaultRequestTimeout());
    }

    @Override
    public boolean isPlaintextConnectionEnabled() {
      return getOrDefault(
          "PlaintextConnectionEnabled",
          ofNullable(properties.getZeebe().getBaseUrl()).map(url -> URI.create(url).getScheme()).map(scheme -> !scheme.contains("s")),
          DEFAULT.isPlaintextConnectionEnabled());
    }

    @Override
    public String getCaCertificatePath() {
      return getOrDefault(
          "CaCertificatePath",
          ofNullable(properties.getZeebe().getCaCertificatePath()),
          DEFAULT.getCaCertificatePath());
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
      // TODO implement this
      return null;
    }

    @Override
    public Duration getKeepAlive() {
      return getOrDefault(
          "KeepAlive", ofNullable(properties.getZeebe().getKeepAlive()), DEFAULT.getKeepAlive());
    }

    @Override
    public List<ClientInterceptor> getInterceptors() {
      // TODO implement this
      return null;
    }

    @Override
    public JsonMapper getJsonMapper() {
      // TODO implement this
      return null;
    }

    @Override
    public String getOverrideAuthority() {
      // TODO implement this
      return null;
    }

    @Override
    public int getMaxMessageSize() {
      // TODO implement this
      return 0;
    }

    @Override
    public ScheduledExecutorService jobWorkerExecutor() {
      // TODO implement this
      return null;
    }

    @Override
    public boolean ownsJobWorkerExecutor() {
      // TODO implement this
      return false;
    }

    @Override
    public boolean getDefaultJobWorkerStreamEnabled() {
      // TODO implement this
      return false;
    }

    @Override
    public boolean useDefaultRetryPolicy() {
      // TODO implement this
      return false;
    }
  }
}
