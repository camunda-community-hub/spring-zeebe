package io.zeebe.spring.client.properties;

import static io.zeebe.spring.client.config.ZeebeClientSpringConfiguration.DEFAULT;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "zeebe.client")
public class ZeebeClientConfigurationProperties implements ZeebeClientProperties {

  @NestedConfigurationProperty private Broker broker = new Broker();

  @NestedConfigurationProperty private Request request = new Request();

  @NestedConfigurationProperty private Sendbuffer sendbuffer = new Sendbuffer();

  @NestedConfigurationProperty private Channel channel = new Channel();

  @NestedConfigurationProperty private Subscription subscription = new Subscription();

  @NestedConfigurationProperty private Worker worker = new Worker();

  @NestedConfigurationProperty private Job job = new Job();

  private Integer threads = DEFAULT.getNumManagementThreads();

  private String defaultTopic = DEFAULT.getDefaultTopic();

  @Data
  public static class Broker {

    private String contactPoint = DEFAULT.getBrokerContactPoint();
  }

  @Data
  public static class Request {

    private Duration timeOut = DEFAULT.getRequestTimeout();
    private Duration blockTime = DEFAULT.getRequestBlocktime();
  }

  @Data
  public static class Sendbuffer {

    private Integer size = DEFAULT.getSendBufferSize();
  }

  @Data
  public static class Channel {

    private Duration keepalive = DEFAULT.getTcpChannelKeepAlivePeriod();
  }

  @Data
  public static class Subscription {

    private Integer threads = DEFAULT.getNumSubscriptionExecutionThreads();
    private Integer buffersize = DEFAULT.getDefaultTopicSubscriptionBufferSize();
  }

  @Data
  public static class Worker {

    private Integer buffersize = DEFAULT.getDefaultJobSubscriptionBufferSize();
  }

  @Data
  public static class Job {

    private String worker = DEFAULT.getDefaultJobWorkerName();
    private Duration timeout = DEFAULT.getDefaultJobTimeout();
  }

  @Override
  public String getBrokerContactPoint() {
    return broker.contactPoint;
  }

  @Override
  public Duration getRequestTimeout() {
    return request.timeOut;
  }

  @Override
  public Duration getRequestBlocktime() {
    return request.blockTime;
  }

  @Override
  public int getSendBufferSize() {
    return sendbuffer.getSize();
  }

  @Override
  public int getNumManagementThreads() {
    return threads;
  }

  @Override
  public int getNumSubscriptionExecutionThreads() {
    return subscription.threads;
  }

  @Override
  public int getDefaultTopicSubscriptionBufferSize() {
    return subscription.buffersize;
  }

  @Override
  public int getDefaultJobSubscriptionBufferSize() {
    return worker.buffersize;
  }

  @Override
  public Duration getTcpChannelKeepAlivePeriod() {
    return channel.keepalive;
  }

  @Override
  public String getDefaultJobWorkerName() {
    return job.worker;
  }

  @Override
  public Duration getDefaultJobTimeout() {
    return job.timeout;
  }
}
