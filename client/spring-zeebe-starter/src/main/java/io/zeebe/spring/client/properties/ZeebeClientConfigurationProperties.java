package io.zeebe.spring.client.properties;

import static io.zeebe.spring.client.config.ZeebeClientSpringConfiguration.DEFAULT;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import io.grpc.ClientInterceptor;
import io.zeebe.client.CredentialsProvider;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "zeebe.client")
public class ZeebeClientConfigurationProperties implements ZeebeClientProperties {

  @NestedConfigurationProperty
  private Broker broker = new Broker();

  @NestedConfigurationProperty
  private Worker worker = new Worker();

  @NestedConfigurationProperty
  private Message message = new Message();

  @NestedConfigurationProperty
  private Security security = new Security();

  @NestedConfigurationProperty
  private Job job = new Job();
  
  /**
   * TODO: Think about how to support this in Spring Boot and potentially even remove it from the ZeebeClientProperties
   * interface upstream
   */
  private ArrayList<ClientInterceptor> interceptors = new ArrayList<>();

  private Duration requestTimeout = DEFAULT.getDefaultRequestTimeout();

  @Data
  public static class Broker {
    private String contactPoint = DEFAULT.getBrokerContactPoint();
    private Duration keepAlive = DEFAULT.getKeepAlive();
  }

  @Data
  public static class Worker {
    private Integer maxJobsActive = DEFAULT.getDefaultJobWorkerMaxJobsActive();
    private Integer threads = DEFAULT.getNumJobWorkerExecutionThreads();
    private String defaultName = DEFAULT.getDefaultJobWorkerName();
    private String defaultType = null;
  }

  @Data
  public static class Job {
    private Duration timeout = DEFAULT.getDefaultJobTimeout();
    private Duration pollInterval = DEFAULT.getDefaultJobPollInterval();
  }

  @Data
  public static class Message {
    private Duration timeToLive = DEFAULT.getDefaultMessageTimeToLive();
  }

  @Data
  public static class Security{
    private boolean plaintext = DEFAULT.isPlaintextConnectionEnabled();
    private String certPath = DEFAULT.getCaCertificatePath();
  }

  @Override
  public String getBrokerContactPoint() {
    return broker.getContactPoint();
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
  public CredentialsProvider getCredentialsProvider() {
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
  
}
