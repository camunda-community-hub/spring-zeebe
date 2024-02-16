package io.camunda.zeebe.spring.client.properties;

import static java.util.Optional.*;

import io.camunda.common.auth.Authentication;
import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.ZeebeGatewayProperties;
import io.grpc.ClientInterceptor;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaClientPropertiesTest {
  private static Authentication fromProperties(AuthProperties authProperties) {

  }
  private static class ZeebeClientConfig implements ZeebeClientConfiguration {
    public static final ZeebeClientBuilderImpl DEFAULT =
        (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());
    private static final Logger LOG = LoggerFactory.getLogger(ZeebeClientConfig.class);
    private final ZeebeGatewayProperties properties;

    public ZeebeClientConfig(ZeebeGatewayProperties properties) {
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
          "GatewayAddress", ofNullable(properties.getBaseUrl()), DEFAULT.getGatewayAddress());
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
          ofNullable(properties.getExecutionThreads()),
          DEFAULT.getNumJobWorkerExecutionThreads());
    }

    @Override
    public int getDefaultJobWorkerMaxJobsActive() {
      return getOrDefault(
          "DefaultJobWorkerMaxJobsActive",
          ofNullable(properties.getMaxJobsActive()),
          DEFAULT.getDefaultJobWorkerMaxJobsActive());
    }

    @Override
    public String getDefaultJobWorkerName() {
      return getOrDefault(
          "DefaultJobWorkerName",
          ofNullable(properties.getJobWorkerName()),
          DEFAULT.getDefaultJobWorkerName());
    }

    @Override
    public Duration getDefaultJobTimeout() {
      return getOrDefault(
          "DefaultJobTimeout",
          ofNullable(properties.getJobTimeout()),
          DEFAULT.getDefaultJobTimeout());
    }

    @Override
    public Duration getDefaultJobPollInterval() {
      return getOrDefault(
          "DefaultJobPollInterval",
          ofNullable(properties.getJobPollInterval()),
          DEFAULT.getDefaultJobPollInterval());
    }

    @Override
    public Duration getDefaultMessageTimeToLive() {
      return getOrDefault(
          "DefaultMessageTimeToLive",
          ofNullable(properties.getMessageTimeToLive()),
          DEFAULT.getDefaultMessageTimeToLive());
    }

    @Override
    public Duration getDefaultRequestTimeout() {
      return getOrDefault(
          "DefaultRequestTimeout",
          ofNullable(properties.getRequestTimeout()),
          DEFAULT.getDefaultRequestTimeout());
    }

    @Override
    public boolean isPlaintextConnectionEnabled() {
      return getOrDefault(
          "PlaintextConnectionEnabled",
          ofNullable(properties.getPlaintext()),
          DEFAULT.isPlaintextConnectionEnabled());
    }

    @Override
    public String getCaCertificatePath() {
      return getOrDefault(
          "CaCertificatePath",
          ofNullable(properties.getCaCertificatePath()),
          DEFAULT.getCaCertificatePath());
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
      return null;
    }

    @Override
    public Duration getKeepAlive() {
      return getOrDefault("KeepAlive",ofNullable(properties.getKeepAlive()),DEFAULT.getKeepAlive());
    }

    @Override
    public List<ClientInterceptor> getInterceptors() {
      return null;
    }

    @Override
    public JsonMapper getJsonMapper() {
      return null;
    }

    @Override
    public String getOverrideAuthority() {
      return null;
    }

    @Override
    public int getMaxMessageSize() {
      return 0;
    }

    @Override
    public ScheduledExecutorService jobWorkerExecutor() {
      return null;
    }

    @Override
    public boolean ownsJobWorkerExecutor() {
      return false;
    }

    @Override
    public boolean getDefaultJobWorkerStreamEnabled() {
      return false;
    }

    @Override
    public boolean useDefaultRetryPolicy() {
      return false;
    }
  }
}
