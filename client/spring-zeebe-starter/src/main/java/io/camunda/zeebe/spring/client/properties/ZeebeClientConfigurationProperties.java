package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.client.impl.util.Environment;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.grpc.ClientInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@ConfigurationProperties(prefix = "zeebe.client")
public class ZeebeClientConfigurationProperties implements ZeebeClientProperties {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  // Used to read default config values
  public static final ZeebeClientBuilderImpl DEFAULT = (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());

  private final org.springframework.core.env.Environment environment;

  public static final String CONNECTION_MODE_CLOUD = "CLOUD";
  public static final String CONNECTION_MODE_ADDRESS = "ADDRESS";
  /**
   * Connection mode can be set to "CLOUD" (connect to SaaS with properties), or "ADDRESS" (to use a manually set address to the broker)
   * If not set, "CLOUD" is used if a `zeebe.client.cloud.cluster-id` property is set, "ADDRESS" otherwise.
   */
  private String connectionMode;

  private boolean enabled = true;

  @NestedConfigurationProperty
  private Broker broker = new Broker();

  @NestedConfigurationProperty
  private Cloud cloud = new Cloud();

  @NestedConfigurationProperty
  private Worker worker = new Worker();

  @NestedConfigurationProperty
  private Message message = new Message();

  @NestedConfigurationProperty
  private Security security = new Security();

  @NestedConfigurationProperty
  private Job job = new Job();

  @Lazy
  @Autowired
  private JsonMapper jsonMapper;

  /**
   * TODO: Think about how to support this in Spring Boot and potentially even remove it from the ZeebeClientProperties
   * interface upstream
   */
  private ArrayList<ClientInterceptor> interceptors = new ArrayList<>();

  public ZeebeClientConfigurationProperties(@Autowired org.springframework.core.env.Environment environment) {
    this.environment = environment;
  }

  /**
   * Make sure environment variables and other legacy config options are taken into account.
   * Environment variables are taking precendence over Spring properties.
   * Legacy config options are read only if no real property is set
   */
  @PostConstruct
  public void applyOverrides() {
    if (Environment.system().isDefined("ZEEBE_INSECURE_CONNECTION")) {
      security.plaintext = Environment.system().getBoolean("ZEEBE_INSECURE_CONNECTION");
    }
    if (Environment.system().isDefined("ZEEBE_CA_CERTIFICATE_PATH")) {
      security.certPath = Environment.system().get("ZEEBE_CA_CERTIFICATE_PATH");
    }
    if (Environment.system().isDefined("ZEEBE_KEEP_ALIVE")) {
      broker.keepAlive = Duration.ofMillis(Long.parseUnsignedLong(Environment.system().get("ZEEBE_KEEP_ALIVE")));
    }
    if (Environment.system().isDefined("ZEEBE_OVERRIDE_AUTHORITY")) {
      security.overrideAuthority = Environment.system().get("ZEEBE_OVERRIDE_AUTHORITY");
    }

    if (broker.gatewayAddress==null && environment.containsProperty("zeebe.client.gateway.address")) {
      broker.gatewayAddress = environment.getProperty("zeebe.client.gateway.address");
    }
    if (broker.gatewayAddress==null && environment.containsProperty("zeebe.client.gateway.address")) {
      broker.gatewayAddress = environment.getProperty("zeebe.client.gateway.address");
    }
    if (cloud.clientSecret==null && environment.containsProperty("zeebe.client.cloud.secret")) {
      cloud.clientSecret = environment.getProperty("zeebe.client.cloud.secret");
    }
    if (worker.defaultName ==null && environment.containsProperty("zeebe.client.worker.name")) {
      worker.defaultName = environment.getProperty("zeebe.client.worker.name");
    }
  }

  private Duration requestTimeout = DEFAULT.getDefaultRequestTimeout();

  public Broker getBroker() {
    return broker;
  }

  public void setBroker(Broker broker) {
    this.broker = broker;
  }

  public Cloud getCloud() {
    return cloud;
  }

  public void setCloud(Cloud cloud) {
    this.cloud = cloud;
  }

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

  public void setInterceptors(ArrayList<ClientInterceptor> interceptors) {
    this.interceptors = interceptors;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ZeebeClientConfigurationProperties that = (ZeebeClientConfigurationProperties) o;
    return Objects.equals(broker, that.broker) &&
      Objects.equals(cloud, that.cloud) &&
      Objects.equals(worker, that.worker) &&
      Objects.equals(message, that.message) &&
      Objects.equals(security, that.security) &&
      Objects.equals(job, that.job) &&
      Objects.equals(interceptors, that.interceptors) &&
      Objects.equals(requestTimeout, that.requestTimeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(broker, cloud, worker, message, security, job, interceptors, requestTimeout);
  }

  @Override
  public String toString() {
    return "ZeebeClientConfigurationProperties{" +
      "broker=" + broker +
      ", cloud=" + cloud +
      ", worker=" + worker +
      ", message=" + message +
      ", security=" + security +
      ", job=" + job +
      ", interceptors=" + interceptors +
      ", requestTimeout=" + requestTimeout +
      '}';
  }

  public static class Broker {

    private String gatewayAddress;
    private Duration keepAlive = DEFAULT.getKeepAlive();

    /**
     * Use gatewayAddress. It's deprecated since 0.25.0, and will be removed in 0.26.0
     *
     * @return gatewayAddress
     */
    @Deprecated
    public String getContactPoint() {
      return getGatewayAddress();
    }

    /**
     * Use gatewayAddress. It's deprecated since 0.25.0, and will be removed in 0.26.0
     *
     * @param contactPoint
     */
    @Deprecated
    public void setContactPoint(String contactPoint) {
      setGatewayAddress(contactPoint);
    }

    public String getGatewayAddress() {
      if (gatewayAddress != null) {
        return gatewayAddress;
      } else {
        return DEFAULT.getGatewayAddress();
      }
    }

    public void setGatewayAddress(String gatewayAddress) {
      this.gatewayAddress = gatewayAddress;
    }

    public Duration getKeepAlive() {
      return keepAlive;
    }

    public void setKeepAlive(Duration keepAlive) {
      this.keepAlive = keepAlive;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Broker broker = (Broker) o;
      return Objects.equals(gatewayAddress, broker.gatewayAddress) &&
        Objects.equals(keepAlive, broker.keepAlive);
    }

    @Override
    public int hashCode() {
      return Objects.hash(gatewayAddress, keepAlive);
    }

    @Override
    public String toString() {
      return "Broker{" +
        "gatewayAddress='" + gatewayAddress + '\'' +
        ", keepAlive=" + keepAlive +
        '}';
    }
  }

  public static class Cloud {
    private String clusterId;
    private String clientId;
    private String clientSecret;
    private String region = "bru-2";

    private String baseUrl = "zeebe.camunda.io";
    private String authUrl = "https://login.cloud.camunda.io/oauth/token";
    private int port = 443;
    private String credentialsCachePath;

    public String getClusterId() {
      return clusterId;
    }

    public void setClusterId(String clusterId) {
      this.clusterId = clusterId;
    }

    public String getClientId() {
      return clientId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public String getClientSecret() {
      return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
    }

    public String getRegion() {
      return region;
    }

    public void setRegion(final String region) {
      this.region = region;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getAuthUrl() {
      return authUrl;
    }

    public void setAuthUrl(String authUrl) {
      this.authUrl = authUrl;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getCredentialsCachePath() {
      return credentialsCachePath;
    }

    public void setCredentialsCachePath(String credentialsCachePath) {
      this.credentialsCachePath = credentialsCachePath;
    }

    public String getAudience() {
      return String.format("%s.%s.%s", clusterId, region, baseUrl);
    }

    public boolean isConfigured() {
      return (clusterId != null);
    }

    public String getGatewayAddress() {
      return String.format("%s.%s.%s:%d", clusterId, region, baseUrl, port);
    }
  }

  public static class Worker {
    private Integer maxJobsActive = DEFAULT.getDefaultJobWorkerMaxJobsActive();
    private Integer threads = DEFAULT.getNumJobWorkerExecutionThreads();
    private String defaultName = null; // setting NO default in Spring, as bean/method name is used as default
    private String defaultType = null;
    private Map<String, ZeebeWorkerValue> override = new HashMap<>();

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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Worker worker = (Worker) o;
      return Objects.equals(maxJobsActive, worker.maxJobsActive) &&
        Objects.equals(threads, worker.threads) &&
        Objects.equals(defaultName, worker.defaultName) &&
        Objects.equals(defaultType, worker.defaultType) &&
        Objects.equals(override, worker.override);
    }

    @Override
    public int hashCode() {
      return Objects.hash(maxJobsActive, threads, defaultName, defaultType, override);
    }

    @Override
    public String toString() {
      return "Worker{" +
        "maxJobsActive=" + maxJobsActive +
        ", threads=" + threads +
        ", defaultName='" + defaultName + '\'' +
        ", defaultType='" + defaultType + '\'' +
        ", override=" + override +
        '}';
    }
  }

  public static class Job {
    private Duration timeout = DEFAULT.getDefaultJobTimeout();
    private Duration pollInterval = DEFAULT.getDefaultJobPollInterval();

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
      return Objects.equals(timeout, job.timeout) &&
        Objects.equals(pollInterval, job.pollInterval);
    }

    @Override
    public int hashCode() {
      return Objects.hash(timeout, pollInterval);
    }

    @Override
    public String toString() {
      return "Job{" +
        "timeout=" + timeout +
        ", pollInterval=" + pollInterval +
        '}';
    }
  }

  public static class Message {
    private Duration timeToLive = DEFAULT.getDefaultMessageTimeToLive();

    public Duration getTimeToLive() {
      return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
      this.timeToLive = timeToLive;
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

    @Override
    public String toString() {
      return "Message{" +
        "timeToLive=" + timeToLive +
        '}';
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
      return plaintext == security.plaintext && Objects.equals(overrideAuthority, security.overrideAuthority) && Objects.equals(certPath, security.certPath);
    }

    @Override
    public int hashCode() {
      return Objects.hash(plaintext, overrideAuthority, certPath);
    }

    @Override
    public String toString() {
      return "Security{" +
        "plaintext=" + plaintext +
        ", overrideAuthority='" + overrideAuthority + '\'' +
        ", certPath='" + certPath + '\'' +
        '}';
    }
  }

  @Override
  public String getGatewayAddress() {
    if (connectionMode!=null && connectionMode.length()>0) {
      LOGGER.info("Using connection mode '{}' to connect to Zeebe", connectionMode);
      if (CONNECTION_MODE_CLOUD.equalsIgnoreCase(connectionMode)) {
        return cloud.getGatewayAddress();
      } else if (CONNECTION_MODE_ADDRESS.equalsIgnoreCase(connectionMode)) {
        return broker.getGatewayAddress();
      } else {
        throw new RuntimeException("Value '"+connectionMode+"' for ConnectionMode is invalid, valid values are " + CONNECTION_MODE_CLOUD + " or " + CONNECTION_MODE_ADDRESS);
      }
    } else if (cloud.isConfigured()) {
      return cloud.getGatewayAddress();
    } else {
      return broker.getGatewayAddress();
    }
  }

  public String getConnectionMode() {
    return connectionMode;
  }

  public void setConnectionMode(String connectionMode) {
    this.connectionMode = connectionMode;
  }

  @Override
  public Duration getDefaultRequestTimeout() {
    return getRequestTimeout();
  }

  @Override
  public int getNumJobWorkerExecutionThreads() {
    return worker.getThreads();
  }

  @Override
  public int getDefaultJobWorkerMaxJobsActive() {
    return worker.getMaxJobsActive();
  }

  @Override
  public String getDefaultJobWorkerName() {
    return worker.getDefaultName();
  }

  public String getDefaultJobWorkerType() {
    return worker.getDefaultType();
  }

  @Override
  public Duration getDefaultJobTimeout() {
    return job.getTimeout();
  }

  @Override
  public Duration getDefaultJobPollInterval() {
    return job.getPollInterval();
  }

  @Override
  public Duration getDefaultMessageTimeToLive() {
    return message.getTimeToLive();
  }

  @Override
  public boolean isPlaintextConnectionEnabled() {
    return security.isPlaintext();
  }

  @Override
  public String getCaCertificatePath() {
    return security.getCertPath();
  }

  @Override
  public String getOverrideAuthority() {
    return security.getOverrideAuthority();
  }

  @Override
  public CredentialsProvider getCredentialsProvider() {
    if (cloud.clientId != null && cloud.clientSecret != null) {
//        log.debug("Client ID and secret are configured. Creating OAuthCredientialsProvider with: {}", this);
      return CredentialsProvider.newCredentialsProviderBuilder()
        .clientId(cloud.clientId)
        .clientSecret(cloud.clientSecret)
        .audience(cloud.getAudience())
        .authorizationServerUrl(cloud.authUrl)
        .credentialsCachePath(cloud.credentialsCachePath)
        .build();
    } else if (Environment.system().get("ZEEBE_CLIENT_ID") != null && Environment.system().get("ZEEBE_CLIENT_SECRET") != null) {
      // Copied from ZeebeClientBuilderImpl
      OAuthCredentialsProviderBuilder builder = CredentialsProvider.newCredentialsProviderBuilder();
      int separatorIndex = broker.gatewayAddress.lastIndexOf(58); //":"
      if (separatorIndex > 0) {
        builder.audience(broker.gatewayAddress.substring(0, separatorIndex));
      }
      return builder.build();
    }
    return null;
  }

  @Override
  public Duration getKeepAlive() {
    return broker.getKeepAlive();
  }

  @Override
  public List<ClientInterceptor> getInterceptors() {
    return interceptors;
  }

  @Override
  public JsonMapper getJsonMapper() {
    return jsonMapper;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }
}
