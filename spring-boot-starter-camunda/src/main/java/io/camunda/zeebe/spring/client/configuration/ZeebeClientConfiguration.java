package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.DefaultNoopAuthentication;
import io.camunda.common.auth.Product;
import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.client.impl.util.Environment;
import io.camunda.zeebe.spring.client.jobhandling.ZeebeClientExecutorService;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.springframework.util.StringUtils.hasText;

public class ZeebeClientConfiguration implements io.camunda.zeebe.client.ZeebeClientConfiguration {

  @Autowired
  private ZeebeClientConfigurationProperties properties;

  @Autowired
  private Authentication authentication;

  @Lazy // Must be lazy, otherwise we get circular dependencies on beans
  @Autowired
  private JsonMapper jsonMapper;

  @Lazy
  @Autowired(required = false)
  private List<ClientInterceptor> interceptors;

  @Lazy
  @Autowired
  private ZeebeClientExecutorService zeebeClientExecutorService;

  @PostConstruct
  public void applyLegacy() {
    // make sure environment variables and other legacy config options are taken into account (duplicate, also done by  qPostConstruct, whatever)
    properties.applyOverrides();
  }

  @Override
  public String getGatewayAddress() {
    return properties.getGatewayAddress();
  }

  @Override
  public String getDefaultTenantId() {
    return properties.getDefaultTenantId();
  }

  @Override
  public List<String> getDefaultJobWorkerTenantIds() {
    return properties.getDefaultJobWorkerTenantIds();
  }

  @Override
  public int getNumJobWorkerExecutionThreads() {
    return properties.getNumJobWorkerExecutionThreads();
  }

  @Override
  public int getDefaultJobWorkerMaxJobsActive() {
    return properties.getDefaultJobWorkerMaxJobsActive();
  }

  @Override
  public String getDefaultJobWorkerName() {
    return properties.getDefaultJobWorkerName();
  }

  @Override
  public Duration getDefaultJobTimeout() {
    return properties.getDefaultJobTimeout();
  }

  @Override
  public Duration getDefaultJobPollInterval() {
    return properties.getDefaultJobPollInterval();
  }

  @Override
  public Duration getDefaultMessageTimeToLive() {
    return properties.getDefaultMessageTimeToLive();
  }

  @Override
  public Duration getDefaultRequestTimeout() {
    return properties.getDefaultRequestTimeout();
  }

  @Override
  public boolean isPlaintextConnectionEnabled() {
    return properties.isPlaintextConnectionEnabled();
  }

  @Override
  public String getCaCertificatePath() {
    return properties.getCaCertificatePath();
  }

  @Override
  public CredentialsProvider getCredentialsProvider() {
    if (!(authentication instanceof DefaultNoopAuthentication)) {
      return new CredentialsProvider() {
        @Override
        public void applyCredentials(Metadata headers) throws IOException {
          final var authHeader = authentication.getTokenHeader(Product.ZEEBE);
          final var authHeaderKey = Metadata.Key.of(authHeader.getKey(), Metadata.ASCII_STRING_MARSHALLER);
          headers.put(authHeaderKey, authHeader.getValue());
        }

        @Override
        public boolean shouldRetryRequest(Throwable throwable) {
          return ((StatusRuntimeException) throwable).getStatus() == Status.DEADLINE_EXCEEDED;
        }
      };
    }
    if (hasText(properties.getCloud().getClientId()) && hasText(properties.getCloud().getClientSecret())) {
//        log.debug("Client ID and secret are configured. Creating OAuthCredientialsProvider with: {}", this);
      return CredentialsProvider.newCredentialsProviderBuilder()
        .clientId(properties.getCloud().getClientId())
        .clientSecret(properties.getCloud().getClientSecret())
        .audience(properties.getCloud().getAudience())
        .scope(properties.getCloud().getScope())
        .authorizationServerUrl(properties.getCloud().getAuthUrl())
        .credentialsCachePath(properties.getCloud().getCredentialsCachePath())
        .build();
    }
    if (Environment.system().get("ZEEBE_CLIENT_ID") != null && Environment.system().get("ZEEBE_CLIENT_SECRET") != null) {
      // Copied from ZeebeClientBuilderImpl
      OAuthCredentialsProviderBuilder builder = CredentialsProvider.newCredentialsProviderBuilder();
      int separatorIndex = properties.getBroker().getGatewayAddress().lastIndexOf(58); //":"
      if (separatorIndex > 0) {
        builder.audience(properties.getBroker().getGatewayAddress().substring(0, separatorIndex));
      }
      return builder.build();
    }
    return null;
  }

  @Override
  public Duration getKeepAlive() {
    return properties.getKeepAlive();
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
    return properties.getOverrideAuthority();
  }

  @Override
  public int getMaxMessageSize() {
    return properties.getMaxMessageSize();
  }

  @Override
  public ScheduledExecutorService jobWorkerExecutor() {
    return zeebeClientExecutorService.get();
  }

  @Override
  public boolean ownsJobWorkerExecutor() {
    return properties.ownsJobWorkerExecutor();
  }

  @Override
  public boolean getDefaultJobWorkerStreamEnabled() {
    return properties.getDefaultJobWorkerStreamEnabled();
  }

  @Override
  public boolean useDefaultRetryPolicy() {
    return properties.useDefaultRetryPolicy();
  }

}
