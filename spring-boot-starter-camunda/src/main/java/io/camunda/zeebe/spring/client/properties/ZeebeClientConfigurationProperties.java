package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.client.ClientProperties;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.client.impl.util.Environment;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "zeebe.client")
@Deprecated
public class ZeebeClientConfigurationProperties {

  // Used to read default config values
  public static final ZeebeClientBuilderImpl DEFAULT =
      (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());
  public static final String CONNECTION_MODE_CLOUD = "CLOUD";
  public static final String CONNECTION_MODE_ADDRESS = "ADDRESS";
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final org.springframework.core.env.Environment environment;

  /**
   * Connection mode can be set to "CLOUD" (connect to SaaS with properties), or "ADDRESS" (to use a
   * manually set address to the broker) If not set, "CLOUD" is used if a
   * `zeebe.client.cloud.cluster-id` property is set, "ADDRESS" otherwise.
   */
  private String connectionMode;

  private String defaultTenantId = DEFAULT.getDefaultTenantId();

  @Deprecated private List<String> defaultJobWorkerTenantIds;

  private boolean applyEnvironmentVariableOverrides =
      false; // the default is NOT to overwrite anything by environment variables in a Spring Boot
  // world - it is unintuitive

  private boolean enabled = true;

  @NestedConfigurationProperty private Broker broker = new Broker();

  @NestedConfigurationProperty private Cloud cloud = new Cloud();

  @NestedConfigurationProperty private Worker worker = new Worker();

  @NestedConfigurationProperty private Message message = new Message();

  @NestedConfigurationProperty private Security security = new Security();

  @NestedConfigurationProperty private Job job = new Job();

  @Deprecated private boolean ownsJobWorkerExecutor;

  @Deprecated
  private boolean defaultJobWorkerStreamEnabled = DEFAULT.getDefaultJobWorkerStreamEnabled();

  private Duration requestTimeout = DEFAULT.getDefaultRequestTimeout();

  @Autowired
  public ZeebeClientConfigurationProperties(org.springframework.core.env.Environment environment) {
    this.environment = environment;
  }

  /**
   * Make sure environment variables and other legacy config options are taken into account.
   * Environment variables are taking precedence over Spring properties. Legacy config options are
   * read only if no real property is set
   */
  @PostConstruct
  public void applyOverrides() {
    if (isApplyEnvironmentVariableOverrides()) {
      if (Environment.system().isDefined("ZEEBE_INSECURE_CONNECTION")) {
        security.plaintext = Environment.system().getBoolean("ZEEBE_INSECURE_CONNECTION");
      }
      if (Environment.system().isDefined("ZEEBE_CA_CERTIFICATE_PATH")) {
        security.certPath = Environment.system().get("ZEEBE_CA_CERTIFICATE_PATH");
      }
      if (Environment.system().isDefined("ZEEBE_KEEP_ALIVE")) {
        broker.keepAlive =
            Duration.ofMillis(Long.parseUnsignedLong(Environment.system().get("ZEEBE_KEEP_ALIVE")));
      }
      if (Environment.system().isDefined("ZEEBE_OVERRIDE_AUTHORITY")) {
        security.overrideAuthority = Environment.system().get("ZEEBE_OVERRIDE_AUTHORITY");
      }
    }

    if (environment != null) {
      // Environment==null can happen in test cases where the environment is not set
      // Java Client has some name differences in properties - support those as well in case people
      // use those (https://github.com/camunda-community-hub/spring-zeebe/issues/350)
      if (broker.gatewayAddress == null
          || DEFAULT.getGatewayAddress().equals(broker.gatewayAddress)
              && environment.containsProperty(ClientProperties.GATEWAY_ADDRESS)) {
        broker.gatewayAddress = environment.getProperty(ClientProperties.GATEWAY_ADDRESS);
      }
      if (cloud.clientSecret == null
          && environment.containsProperty(ClientProperties.CLOUD_CLIENT_SECRET)) {
        cloud.clientSecret = environment.getProperty(ClientProperties.CLOUD_CLIENT_SECRET);
      }
      if (worker.defaultName == null
          && environment.containsProperty(ClientProperties.DEFAULT_JOB_WORKER_NAME)) {
        worker.defaultName = environment.getProperty(ClientProperties.DEFAULT_JOB_WORKER_NAME);
      }
      // Support environment based default tenant id override if value is client default fallback
      if ((defaultTenantId == null || defaultTenantId.equals(DEFAULT.getDefaultTenantId()))
          && environment.containsProperty(ClientProperties.DEFAULT_TENANT_ID)) {
        defaultTenantId = environment.getProperty(ClientProperties.DEFAULT_TENANT_ID);
      }
    }
    // Support default job worker tenant ids based on the default tenant id
    if (worker.getDefaultTenantIds() == null && defaultTenantId != null) {
      worker.setDefaultTenantIds(Collections.singletonList(defaultTenantId));
    }
  }
@DeprecatedConfigurationProperty
  public Broker getBroker() {
    return broker;
  }
  @DeprecatedConfigurationProperty

  public void setBroker(Broker broker) {
    this.broker = broker;
  }
  @DeprecatedConfigurationProperty

  public Cloud getCloud() {
    return cloud;
  }
  @DeprecatedConfigurationProperty

  public void setCloud(Cloud cloud) {
    this.cloud = cloud;
  }
  @DeprecatedConfigurationProperty

  public Worker getWorker() {
    return worker;
  }

  public void setWorker(Worker worker) {
    this.worker = worker;
  }

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public Security getSecurity() {
    return security;
  }

  public void setSecurity(Security security) {
    this.security = security;
  }

  public Job getJob() {
    return job;
  }

  public void setJob(Job job) {
    this.job = job;
  }

  public Duration getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isApplyEnvironmentVariableOverrides() {
    return applyEnvironmentVariableOverrides;
  }

  public void setApplyEnvironmentVariableOverrides(boolean applyEnvironmentVariableOverrides) {
    this.applyEnvironmentVariableOverrides = applyEnvironmentVariableOverrides;
  }

  /**
   * @deprecated use getWorker().setOwnsExecutor() instead
   */
  @Deprecated
  public void setOwnsJobWorkerExecutor(boolean ownsJobWorkerExecutor) {
    this.ownsJobWorkerExecutor = ownsJobWorkerExecutor;
  }

  /**
   * @deprecated use getWorker().isOwnsExecutor() instead
   */
  @Deprecated
  public boolean ownsJobWorkerExecutor() {
    return ownsJobWorkerExecutor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ZeebeClientConfigurationProperties that = (ZeebeClientConfigurationProperties) o;
    return Objects.equals(broker, that.broker)
        && Objects.equals(cloud, that.cloud)
        && Objects.equals(worker, that.worker)
        && Objects.equals(message, that.message)
        && Objects.equals(security, that.security)
        && Objects.equals(job, that.job)
        && Objects.equals(requestTimeout, that.requestTimeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(broker, cloud, worker, message, security, job, requestTimeout);
  }

  @Override
  public String toString() {
    return "ZeebeClientConfigurationProperties{"
        + "environment="
        + environment
        + ", connectionMode='"
        + connectionMode
        + '\''
        + ", defaultTenantId='"
        + defaultTenantId
        + '\''
        + ", defaultJobWorkerTenantIds="
        + defaultJobWorkerTenantIds
        + ", applyEnvironmentVariableOverrides="
        + applyEnvironmentVariableOverrides
        + ", enabled="
        + enabled
        + ", broker="
        + broker
        + ", cloud="
        + cloud
        + ", worker="
        + worker
        + ", message="
        + message
        + ", security="
        + security
        + ", job="
        + job
        + ", ownsJobWorkerExecutor="
        + ownsJobWorkerExecutor
        + ", defaultJobWorkerStreamEnabled="
        + defaultJobWorkerStreamEnabled
        + ", requestTimeout="
        + requestTimeout
        + '}';
  }

  /**
   * @deprecated use PropertiesUtil.getZeebeGatewayAddress() instead
   */
  @Deprecated
  public String getGatewayAddress() {
    return PropertiesUtil.getZeebeGatewayAddress(this);
  }

  public String getDefaultTenantId() {
    return defaultTenantId;
  }

  public void setDefaultTenantId(String defaultTenantId) {
    this.defaultTenantId = defaultTenantId;
  }

  /**
   * @deprecated use getWorker().getDefaultTenantIds() instead
   */
  @Deprecated
  public List<String> getDefaultJobWorkerTenantIds() {
    return defaultJobWorkerTenantIds;
  }

  /**
   * @deprecated use getWorker().setDefaultTenantIds() instead
   */
  @Deprecated
  public void setDefaultJobWorkerTenantIds(List<String> defaultJobWorkerTenantIds) {
    this.defaultJobWorkerTenantIds = defaultJobWorkerTenantIds;
  }

  /**
   * @deprecated use getWorker().isDefaultStreamEnabled() instead
   */
  @Deprecated
  public boolean getDefaultJobWorkerStreamEnabled() {
    return defaultJobWorkerStreamEnabled;
  }

  /**
   * @deprecated use getWorker().setDefaultStreamEnabled() instead
   */
  @Deprecated
  public void setDefaultJobWorkerStreamEnabled(boolean defaultJobWorkerStreamEnabled) {
    this.defaultJobWorkerStreamEnabled = defaultJobWorkerStreamEnabled;
  }

  public boolean useDefaultRetryPolicy() {
    return false;
  }

  public String getConnectionMode() {
    return connectionMode;
  }

  public void setConnectionMode(String connectionMode) {
    this.connectionMode = connectionMode;
  }

  /**
   * @deprecated use getRequestTimeout() instead
   */
  @Deprecated
  public Duration getDefaultRequestTimeout() {
    return getRequestTimeout();
  }

  /**
   * @deprecated use getWorker().getThreads() instead
   */
  @Deprecated
  public int getNumJobWorkerExecutionThreads() {
    return worker.getThreads();
  }

  /**
   * @deprecated use getWorker().getMaxJobsActive() instead
   */
  @Deprecated
  public int getDefaultJobWorkerMaxJobsActive() {
    return worker.getMaxJobsActive();
  }

  /**
   * @deprecated use getWorker().getDefaultName() instead
   */
  @Deprecated
  public String getDefaultJobWorkerName() {
    return worker.getDefaultName();
  }

  /**
   * @deprecated use getWorker().getDefaultType() instead
   */
  @Deprecated
  public String getDefaultJobWorkerType() {
    return worker.getDefaultType();
  }

  /**
   * @deprecated use getDuration().getTimeout() instead
   */
  @Deprecated
  public Duration getDefaultJobTimeout() {
    return job.getTimeout();
  }

  /**
   * @deprecated use getJob().getPollInterval() instead
   */
  @Deprecated
  public Duration getDefaultJobPollInterval() {
    return job.getPollInterval();
  }

  /**
   * @deprecated use getMessage().getTimeToLive() instead
   */
  @Deprecated
  public Duration getDefaultMessageTimeToLive() {
    return message.getTimeToLive();
  }

  /**
   * @deprecated use getSecurity().isPlainText() instead
   */
  @Deprecated
  public boolean isPlaintextConnectionEnabled() {
    return security.isPlaintext();
  }

  /**
   * @deprecated use getSecurity().getCertPath() instead
   */
  @Deprecated
  public String getCaCertificatePath() {
    return security.getCertPath();
  }

  /**
   * @deprecated use getSecurity().getOverrideAuthority() instead
   */
  @Deprecated
  public String getOverrideAuthority() {
    return security.getOverrideAuthority();
  }

  /**
   * @deprecated use getBroker().getKeepAlive() instead
   */
  @Deprecated
  public Duration getKeepAlive() {
    return broker.getKeepAlive();
  }

  /**
   * @deprecated use getMessage().getMessageSize() instead
   */
  @Deprecated
  public int getMaxMessageSize() {
    return message.getMaxMessageSize();
  }

  public static class Broker {
    @Deprecated private String contactPoint;
    private String gatewayAddress = DEFAULT.getGatewayAddress();
    private Duration keepAlive = DEFAULT.getKeepAlive();

    @Override
    public String toString() {
      return "Broker{"
          + "gatewayAddress='"
          + gatewayAddress
          + '\''
          + ", keepAlive="
          + keepAlive
          + '}';
    }

    /**
     * @deprecated use getGatewayAddress() instead
     */
    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.base-url, new property requires URI format")

    public String getContactPoint() {
      return contactPoint;
    }

    /**
     * @deprecated use setGatewayAddress() instead
     */
    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.base-url, new property requires URI format")

    public void setContactPoint(String contactPoint) {
      this.contactPoint = contactPoint;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.base-url, new property requires URI format")

    public String getGatewayAddress() {
      return gatewayAddress;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.base-url, new property requires URI format")

    public void setGatewayAddress(String gatewayAddress) {
      this.gatewayAddress = gatewayAddress;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.keep-alive")

    public Duration getKeepAlive() {
      return keepAlive;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.zeebe.keep-alive")

    public void setKeepAlive(Duration keepAlive) {
      this.keepAlive = keepAlive;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Broker broker = (Broker) o;
      return Objects.equals(gatewayAddress, broker.gatewayAddress)
          && Objects.equals(keepAlive, broker.keepAlive);
    }

    @Override
    public int hashCode() {
      return Objects.hash(gatewayAddress, keepAlive);
    }
  }

  public static class Cloud {

    private String clusterId;
    private String clientId;
    private String clientSecret;
    private String region = "bru-2";
    private String scope;
    private String baseUrl = "zeebe.camunda.io";
    private String authUrl = "https://login.cloud.camunda.io/oauth/token";
    private int port = 443;
    private String credentialsCachePath;

    @Override
    public String toString() {
      return "Cloud{"
          + "clusterId='"
          + clusterId
          + '\''
          + ", clientId='"
          + "***"
          + '\''
          + ", clientSecret='"
          + "***"
          + '\''
          + ", region='"
          + region
          + '\''
          + ", scope='"
          + scope
          + '\''
          + ", baseUrl='"
          + baseUrl
          + '\''
          + ", authUrl='"
          + authUrl
          + '\''
          + ", port="
          + port
          + ", credentialsCachePath='"
          + credentialsCachePath
          + '\''
          + '}';
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.cluster-id")

    public String getClusterId() {
      return clusterId;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.cluster-id")

    public void setClusterId(String clusterId) {
      this.clusterId = clusterId;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.client-id or camunda.client.zeebe.client-id")

    public String getClientId() {
      return clientId;
    }
    @DeprecatedConfigurationProperty(replacement = "camunda.client.auth.client-id")

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }
    @DeprecatedConfigurationProperty

    public String getClientSecret() {
      return clientSecret;
    }
    @DeprecatedConfigurationProperty

    public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
    }
    @DeprecatedConfigurationProperty

    public String getRegion() {
      return region;
    }
    @DeprecatedConfigurationProperty

    public void setRegion(final String region) {
      this.region = region;
    }
    @DeprecatedConfigurationProperty

    public String getScope() {
      return scope;
    }
    @DeprecatedConfigurationProperty

    public void setScope(String scope) {
      this.scope = scope;
    }
    @DeprecatedConfigurationProperty

    public String getBaseUrl() {
      return baseUrl;
    }
    @DeprecatedConfigurationProperty

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }
    @DeprecatedConfigurationProperty

    public String getAuthUrl() {
      return authUrl;
    }
    @DeprecatedConfigurationProperty

    public void setAuthUrl(String authUrl) {
      this.authUrl = authUrl;
    }
    @DeprecatedConfigurationProperty

    public int getPort() {
      return port;
    }
    @DeprecatedConfigurationProperty

    public void setPort(int port) {
      this.port = port;
    }
    @DeprecatedConfigurationProperty

    public String getCredentialsCachePath() {
      return credentialsCachePath;
    }
    @DeprecatedConfigurationProperty

    public void setCredentialsCachePath(String credentialsCachePath) {
      this.credentialsCachePath = credentialsCachePath;
    }
    @DeprecatedConfigurationProperty

    public String getAudience() {
      return String.format("%s.%s.%s", clusterId, region, baseUrl);
    }
    @DeprecatedConfigurationProperty

    public boolean isConfigured() {
      return (clusterId != null);
    }
    @DeprecatedConfigurationProperty

    public String getGatewayAddress() {
      return String.format("%s.%s.%s:%d", clusterId, region, baseUrl, port);
    }
  }

  public static class Worker {
    private Integer maxJobsActive = DEFAULT.getDefaultJobWorkerMaxJobsActive();
    private Integer threads = DEFAULT.getNumJobWorkerExecutionThreads();
    private String defaultName =
        null; // setting NO default in Spring, as bean/method name is used as default
    private String defaultType = null;
    private Map<String, ZeebeWorkerValue> override = new HashMap<>();
    private List<String> defaultTenantIds;
    private boolean defaultStreamEnabled = DEFAULT.getDefaultJobWorkerStreamEnabled();
    private boolean ownsExecutor;

    @Override
    public String toString() {
      return "Worker{"
          + "maxJobsActive="
          + maxJobsActive
          + ", threads="
          + threads
          + ", defaultName='"
          + defaultName
          + '\''
          + ", defaultType='"
          + defaultType
          + '\''
          + ", override="
          + override
          + '}';
    }

    public boolean isOwnsExecutor() {
      return ownsExecutor;
    }

    public void setOwnsExecutor(boolean ownsExecutor) {
      this.ownsExecutor = ownsExecutor;
    }

    public Map<String, ZeebeWorkerValue> getOverride() {
      return override;
    }

    public void setOverride(Map<String, ZeebeWorkerValue> override) {
      this.override = override;
    }

    public Integer getMaxJobsActive() {
      return maxJobsActive;
    }

    public void setMaxJobsActive(Integer maxJobsActive) {
      this.maxJobsActive = maxJobsActive;
    }

    public Integer getThreads() {
      return threads;
    }

    public void setThreads(Integer threads) {
      this.threads = threads;
    }

    public String getDefaultName() {
      return defaultName;
    }

    public void setDefaultName(String defaultName) {
      this.defaultName = defaultName;
    }

    public String getDefaultType() {
      return defaultType;
    }

    public void setDefaultType(String defaultType) {
      this.defaultType = defaultType;
    }

    public List<String> getDefaultTenantIds() {
      return defaultTenantIds;
    }

    public void setDefaultTenantIds(List<String> defaultTenantIds) {
      this.defaultTenantIds = defaultTenantIds;
    }

    public boolean isDefaultStreamEnabled() {
      return defaultStreamEnabled;
    }

    public void setDefaultStreamEnabled(boolean defaultStreamEnabled) {
      this.defaultStreamEnabled = defaultStreamEnabled;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Worker worker = (Worker) o;
      return Objects.equals(maxJobsActive, worker.maxJobsActive)
          && Objects.equals(threads, worker.threads)
          && Objects.equals(defaultName, worker.defaultName)
          && Objects.equals(defaultType, worker.defaultType)
          && Objects.equals(override, worker.override);
    }

    @Override
    public int hashCode() {
      return Objects.hash(maxJobsActive, threads, defaultName, defaultType, override);
    }
  }

  public static class Job {

    private Duration timeout = DEFAULT.getDefaultJobTimeout();
    private Duration pollInterval = DEFAULT.getDefaultJobPollInterval();

    @Override
    public String toString() {
      return "Job{" + "timeout=" + timeout + ", pollInterval=" + pollInterval + '}';
    }

    public Duration getTimeout() {
      return timeout;
    }

    public void setTimeout(Duration timeout) {
      this.timeout = timeout;
    }

    public Duration getPollInterval() {
      return pollInterval;
    }

    public void setPollInterval(Duration pollInterval) {
      this.pollInterval = pollInterval;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Job job = (Job) o;
      return Objects.equals(timeout, job.timeout) && Objects.equals(pollInterval, job.pollInterval);
    }

    @Override
    public int hashCode() {
      return Objects.hash(timeout, pollInterval);
    }
  }

  public static class Message {

    private Duration timeToLive = DEFAULT.getDefaultMessageTimeToLive();
    private int maxMessageSize = DEFAULT.getMaxMessageSize();

    @Override
    public String toString() {
      return "Message{" + "timeToLive=" + timeToLive + ", maxMessageSize=" + maxMessageSize + '}';
    }

    public Duration getTimeToLive() {
      return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
      this.timeToLive = timeToLive;
    }

    public int getMaxMessageSize() {
      return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
      this.maxMessageSize = maxMessageSize;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Message message = (Message) o;
      return Objects.equals(timeToLive, message.timeToLive);
    }

    @Override
    public int hashCode() {
      return Objects.hash(timeToLive);
    }
  }

  public static class Security {

    private boolean plaintext = DEFAULT.isPlaintextConnectionEnabled();
    private String overrideAuthority = DEFAULT.getOverrideAuthority();
    private String certPath = DEFAULT.getCaCertificatePath();

    public boolean isPlaintext() {
      return plaintext;
    }

    public void setPlaintext(boolean plaintext) {
      this.plaintext = plaintext;
    }

    public String getCertPath() {
      return certPath;
    }

    public void setCertPath(String certPath) {
      this.certPath = certPath;
    }

    public String getOverrideAuthority() {
      return overrideAuthority;
    }

    public void setOverrideAuthority(String overrideAuthority) {
      this.overrideAuthority = overrideAuthority;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Security security = (Security) o;
      return plaintext == security.plaintext
          && Objects.equals(overrideAuthority, security.overrideAuthority)
          && Objects.equals(certPath, security.certPath);
    }

    @Override
    public int hashCode() {
      return Objects.hash(plaintext, overrideAuthority, certPath);
    }

    @Override
    public String toString() {
      return "Security{"
          + "plaintext="
          + plaintext
          + ", overrideAuthority='"
          + overrideAuthority
          + '\''
          + ", certPath='"
          + certPath
          + '\''
          + '}';
    }
  }
}
