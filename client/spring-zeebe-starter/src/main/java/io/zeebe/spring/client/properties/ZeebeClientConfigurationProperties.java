package io.zeebe.spring.client.properties;

import static io.zeebe.spring.client.config.ZeebeClientSpringConfiguration.DEFAULT;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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

  private Duration requestTimeout = DEFAULT.getDefaultRequestTimeout();

  @Data
  public static class Broker {
    private String contactPoint = DEFAULT.getBrokerContactPoint();
  }

  @Data
  public static class Worker {
    private Integer maxJobsActive = DEFAULT.getDefaultJobWorkerMaxJobsActive();
    private Integer threads = DEFAULT.getNumJobWorkerExecutionThreads();
  }

  @Data
  public static class Job {
    private String worker = DEFAULT.getDefaultJobWorkerName();
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
    return job.getWorker();
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
  
}
