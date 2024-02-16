package io.camunda.zeebe.spring.client.properties;

import static java.util.Optional.*;

import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.grpc.ClientInterceptor;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaClientPropertiesTest {
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
          ofNullable(properties.getZeebe().getBaseUrl()),
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
          ofNullable(properties.getZeebe().getPlaintext()),
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
