package io.camunda.zeebe.spring.client.configuration;

import static io.camunda.zeebe.spring.client.configuration.PropertyUtil.*;
import static io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties.*;
import static org.springframework.util.StringUtils.hasText;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.DefaultNoopAuthentication;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SimpleAuthentication;
import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.client.impl.util.Environment;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.PropertiesUtil;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ZeebeClientConfiguration implements io.camunda.zeebe.client.ZeebeClientConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(ZeebeClientConfiguration.class);
  private final Map<String, Object> configCache = new HashMap<>();
  private final ZeebeClientConfigurationProperties properties;
  private final CamundaClientProperties camundaClientProperties;
  private final Authentication authentication;
  private final JsonMapper jsonMapper;
  private final List<ClientInterceptor> interceptors;
  private final ZeebeClientExecutorService zeebeClientExecutorService;

  @Autowired
  public ZeebeClientConfiguration(
      ZeebeClientConfigurationProperties properties,
      CamundaClientProperties camundaClientProperties,
      Authentication authentication,
      JsonMapper jsonMapper,
      List<ClientInterceptor> interceptors,
      ZeebeClientExecutorService zeebeClientExecutorService) {
    this.properties = properties;
    this.camundaClientProperties = camundaClientProperties;
    this.authentication = authentication;
    this.jsonMapper = jsonMapper;
    this.interceptors = interceptors;
    this.zeebeClientExecutorService = zeebeClientExecutorService;
  }

  @PostConstruct
  public void applyLegacy() {
    // make sure environment variables and other legacy config options are taken into account
    // (duplicate, also done by  qPostConstruct, whatever)
    properties.applyOverrides();
  }

  @Override
  public String getGatewayAddress() {
    return getOrLegacyOrDefault(
        "GatewayAddress",
        this::composeGatewayAddress,
        () -> PropertiesUtil.getZeebeGatewayAddress(properties),
        DEFAULT.getGatewayAddress(),
        configCache);
  }

  private String composeGatewayAddress() {
    // check if port is set
    if (camundaClientProperties.getZeebe().getBaseUrl().getPort() != -1) {
      String gatewayAddress =
          camundaClientProperties.getZeebe().getBaseUrl().getHost()
              + ":"
              + camundaClientProperties.getZeebe().getBaseUrl().getPort();
      LOG.debug("Gateway port is set, address will be '{}'", gatewayAddress);
      return gatewayAddress;
    }
    // check if default port can be applied
    if (camundaClientProperties.getZeebe().getBaseUrl().getDefaultPort() != -1) {
      String gatewayAddress =
          camundaClientProperties.getZeebe().getBaseUrl().getHost()
              + ":"
              + camundaClientProperties.getZeebe().getBaseUrl().getDefaultPort();
      LOG.debug("Gateway port has default, address will be '{}'", gatewayAddress);
      return gatewayAddress;
    }
    LOG.debug(
        "Gateway cannot be determined, address will be '{}'",
        camundaClientProperties.getZeebe().getBaseUrl().getHost());
    // do not use any port
    return camundaClientProperties.getZeebe().getBaseUrl().getHost();
  }

  @Override
  public String getDefaultTenantId() {
    return getOrLegacyOrDefault(
        "DefaultTenantId",
        prioritized(
            DEFAULT.getDefaultTenantId(),
            Arrays.asList(
                () -> camundaClientProperties.getTenantIds().get(0),
                () -> camundaClientProperties.getZeebe().getDefaults().getTenantIds().get(0))),
        properties::getDefaultTenantId,
        DEFAULT.getDefaultTenantId(),
        configCache);
  }

  @Override
  public List<String> getDefaultJobWorkerTenantIds() {
    return getOrLegacyOrDefault(
        "DefaultJobWorkerTenantIds",
        prioritized(
            DEFAULT.getDefaultJobWorkerTenantIds(),
            Arrays.asList(
                camundaClientProperties::getTenantIds,
                () -> camundaClientProperties.getZeebe().getDefaults().getTenantIds())),
        properties::getDefaultJobWorkerTenantIds,
        DEFAULT.getDefaultJobWorkerTenantIds(),
        configCache);
  }

  @Override
  public int getNumJobWorkerExecutionThreads() {
    return getOrLegacyOrDefault(
        "NumJobWorkerExecutionThreads",
        () -> camundaClientProperties.getZeebe().getExecutionThreads(),
        () -> properties.getWorker().getThreads(),
        DEFAULT.getNumJobWorkerExecutionThreads(),
        configCache);
  }

  @Override
  public int getDefaultJobWorkerMaxJobsActive() {
    return getOrLegacyOrDefault(
        "DefaultJobWorkerMaxJobsActive",
        () -> camundaClientProperties.getZeebe().getDefaults().getMaxJobsActive(),
        () -> properties.getWorker().getMaxJobsActive(),
        DEFAULT.getDefaultJobWorkerMaxJobsActive(),
        configCache);
  }

  @Override
  public String getDefaultJobWorkerName() {
    return getOrLegacyOrDefault(
        "DefaultJobWorkerName",
        () -> camundaClientProperties.getZeebe().getDefaults().getName(),
        () -> properties.getWorker().getDefaultName(),
        DEFAULT.getDefaultJobWorkerName(),
        configCache);
  }

  @Override
  public Duration getDefaultJobTimeout() {
    return getOrLegacyOrDefault(
        "DefaultJobTimeout",
        () -> camundaClientProperties.getZeebe().getDefaults().getTimeout(),
        () -> properties.getJob().getTimeout(),
        DEFAULT.getDefaultJobTimeout(),
        configCache);
  }

  @Override
  public Duration getDefaultJobPollInterval() {
    return getOrLegacyOrDefault(
        "DefaultJobPollInterval",
        () -> camundaClientProperties.getZeebe().getDefaults().getPollInterval(),
        () -> properties.getJob().getPollInterval(),
        DEFAULT.getDefaultJobPollInterval(),
        configCache);
  }

  @Override
  public Duration getDefaultMessageTimeToLive() {
    return getOrLegacyOrDefault(
        "DefaultMessageTimeToLive",
        () -> camundaClientProperties.getZeebe().getMessageTimeToLive(),
        () -> properties.getMessage().getTimeToLive(),
        DEFAULT.getDefaultMessageTimeToLive(),
        configCache);
  }

  @Override
  public Duration getDefaultRequestTimeout() {
    return getOrLegacyOrDefault(
        "DefaultRequestTimeout",
        prioritized(
            DEFAULT.getDefaultRequestTimeout(),
            Arrays.asList(
                () -> camundaClientProperties.getZeebe().getRequestTimeout(),
                () -> camundaClientProperties.getZeebe().getDefaults().getRequestTimeout())),
        properties::getRequestTimeout,
        DEFAULT.getDefaultRequestTimeout(),
        configCache);
  }

  @Override
  public boolean isPlaintextConnectionEnabled() {
    return getOrLegacyOrDefault(
        "PlaintextConnectionEnabled",
        this::composePlaintext,
        () -> properties.getSecurity().isPlaintext(),
        DEFAULT.isPlaintextConnectionEnabled(),
        configCache);
  }

  private boolean composePlaintext() {
    String protocol = camundaClientProperties.getZeebe().getBaseUrl().getProtocol();
    if (protocol.equals("http")) {
      return true;
    }
    if (protocol.equals("https")) {
      return false;
    }
    throw new IllegalStateException(
        String.format(
            "Unrecognized zeebe protocol '%s'",
            camundaClientProperties.getZeebe().getBaseUrl().getProtocol()));
  }

  @Override
  public String getCaCertificatePath() {
    return getOrLegacyOrDefault(
        "CaCertificatePath",
        () -> camundaClientProperties.getZeebe().getCaCertificatePath(),
        () -> properties.getSecurity().getCertPath(),
        DEFAULT.getCaCertificatePath(),
        configCache);
  }

  @Override
  public CredentialsProvider getCredentialsProvider() {
    return getOrLegacyOrDefault(
        "CredentialsProvider",
        this::identityCredentialsProvider,
        this::legacyCredentialsProvider,
        null,
        configCache);
  }

  private CredentialsProvider identityCredentialsProvider() {
    if (authentication instanceof SimpleAuthentication) {
      return null;
    } else if (!(authentication instanceof DefaultNoopAuthentication)) {
      return new IdentityCredentialsProvider(authentication);
    }
    return null;
  }

  private CredentialsProvider legacyCredentialsProvider() {
    if (hasText(properties.getCloud().getClientId())
        && hasText(properties.getCloud().getClientSecret())) {
      //        log.debug("Client ID and secret are configured. Creating OAuthCredientialsProvider
      // with: {}", this);
      return CredentialsProvider.newCredentialsProviderBuilder()
          .clientId(properties.getCloud().getClientId())
          .clientSecret(properties.getCloud().getClientSecret())
          .audience(properties.getCloud().getAudience())
          .scope(properties.getCloud().getScope())
          .authorizationServerUrl(properties.getCloud().getAuthUrl())
          .credentialsCachePath(properties.getCloud().getCredentialsCachePath())
          .build();
    } else if (Environment.system().get("ZEEBE_CLIENT_ID") != null
        && Environment.system().get("ZEEBE_CLIENT_SECRET") != null) {
      // Copied from ZeebeClientBuilderImpl
      OAuthCredentialsProviderBuilder builder = CredentialsProvider.newCredentialsProviderBuilder();
      int separatorIndex = properties.getBroker().getGatewayAddress().lastIndexOf(58); // ":"
      if (separatorIndex > 0) {
        builder.audience(properties.getBroker().getGatewayAddress().substring(0, separatorIndex));
      }
      return builder.build();
    }
    return null;
  }

  @Override
  public Duration getKeepAlive() {
    return getOrLegacyOrDefault(
        "KeepAlive",
        () -> camundaClientProperties.getZeebe().getKeepAlive(),
        () -> properties.getBroker().getKeepAlive(),
        DEFAULT.getKeepAlive(),
        configCache);
  }

  @Override
  public List<ClientInterceptor> getInterceptors() {
    return interceptors;
  }

  @Override
  public JsonMapper getJsonMapper() {
    return jsonMapper;
  }

  @Override
  public String getOverrideAuthority() {
    return getOrLegacyOrDefault(
        "OverrideAuthority",
        () -> camundaClientProperties.getZeebe().getOverrideAuthority(),
        () -> properties.getSecurity().getOverrideAuthority(),
        DEFAULT.getOverrideAuthority(),
        configCache);
  }

  @Override
  public int getMaxMessageSize() {
    return getOrLegacyOrDefault(
        "MaxMessageSize",
        () -> camundaClientProperties.getZeebe().getMaxMessageSize(),
        () -> properties.getMessage().getMaxMessageSize(),
        DEFAULT.getMaxMessageSize(),
        configCache);
  }

  public int getMaxMetadataSize() {
    return getOrLegacyOrDefault(
        "MaxMetadataSize",
        () -> camundaClientProperties.getZeebe().getMaxMetadataSize(),
        null,
        DEFAULT.getMaxMetadataSize(),
        configCache);
  }

  @Override
  public ScheduledExecutorService jobWorkerExecutor() {
    return zeebeClientExecutorService.get();
  }

  @Override
  public boolean ownsJobWorkerExecutor() {
    return getOrLegacyOrDefault(
        "ownsJobWorkerExecutor",
        zeebeClientExecutorService::isOwnedByZeebeClient,
        properties::ownsJobWorkerExecutor,
        DEFAULT.ownsJobWorkerExecutor(),
        configCache);
  }

  @Override
  public boolean getDefaultJobWorkerStreamEnabled() {
    return getOrLegacyOrDefault(
        "DefaultJobWorkerStreamEnabled",
        () -> camundaClientProperties.getZeebe().getDefaults().getStreamEnabled(),
        properties::getDefaultJobWorkerStreamEnabled,
        DEFAULT.getDefaultJobWorkerStreamEnabled(),
        configCache);
  }

  @Override
  public boolean useDefaultRetryPolicy() {
    return false;
  }

  @Override
  public String toString() {
    return "ZeebeClientConfiguration{"
        + "properties="
        + properties
        + ", camundaClientProperties="
        + camundaClientProperties
        + ", authentication="
        + authentication
        + ", jsonMapper="
        + jsonMapper
        + ", interceptors="
        + interceptors
        + ", zeebeClientExecutorService="
        + zeebeClientExecutorService
        + '}';
  }

  public static class IdentityCredentialsProvider implements CredentialsProvider {
    private final Authentication authentication;

    public IdentityCredentialsProvider(Authentication authentication) {
      this.authentication = authentication;
    }

    @Override
    public void applyCredentials(Metadata headers) {
      final Map.Entry<String, String> authHeader = authentication.getTokenHeader(Product.ZEEBE);
      final Metadata.Key<String> authHeaderKey =
          Metadata.Key.of(authHeader.getKey(), Metadata.ASCII_STRING_MARSHALLER);
      headers.put(authHeaderKey, authHeader.getValue());
    }

    @Override
    public boolean shouldRetryRequest(Throwable throwable) {
      return ((StatusRuntimeException) throwable).getStatus() == Status.DEADLINE_EXCEEDED;
    }
  }
}
