package io.camunda.zeebe.spring.client.properties.common;

import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import java.time.Duration;
import java.util.Map;

public class ZeebeClientProperties extends ApiProperties {
  private Integer executionThreads;
  private Duration messageTimeToLive;
  private Integer maxMessageSize;
  private Duration requestTimeout;
  private String caCertificatePath;
  private Duration keepAlive;
  private String overrideAuthority;
  private Boolean ownsJobWorkerExecutor;
  private Boolean defaultRetryPolicy;
  private ZeebeWorkerValue defaults;
  private Map<String, ZeebeWorkerValue> override;

  public ZeebeWorkerValue getDefaults() {
    return defaults;
  }

  public void setDefaults(ZeebeWorkerValue defaults) {
    this.defaults = defaults;
  }

  public Map<String, ZeebeWorkerValue> getOverride() {
    return override;
  }

  public void setOverride(Map<String, ZeebeWorkerValue> override) {
    this.override = override;
  }

  public Integer getExecutionThreads() {
    return executionThreads;
  }

  public void setExecutionThreads(Integer executionThreads) {
    this.executionThreads = executionThreads;
  }

  public Duration getMessageTimeToLive() {
    return messageTimeToLive;
  }

  public void setMessageTimeToLive(Duration messageTimeToLive) {
    this.messageTimeToLive = messageTimeToLive;
  }

  public Duration getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public String getCaCertificatePath() {
    return caCertificatePath;
  }

  public void setCaCertificatePath(String caCertificatePath) {
    this.caCertificatePath = caCertificatePath;
  }

  public Duration getKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(Duration keepAlive) {
    this.keepAlive = keepAlive;
  }

  public String getOverrideAuthority() {
    return overrideAuthority;
  }

  public void setOverrideAuthority(String overrideAuthority) {
    this.overrideAuthority = overrideAuthority;
  }

  public Integer getMaxMessageSize() {
    return maxMessageSize;
  }

  public void setMaxMessageSize(Integer maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }

  public Boolean getOwnsJobWorkerExecutor() {
    return ownsJobWorkerExecutor;
  }

  public void setOwnsJobWorkerExecutor(Boolean ownsJobWorkerExecutor) {
    this.ownsJobWorkerExecutor = ownsJobWorkerExecutor;
  }

  public Boolean getDefaultRetryPolicy() {
    return defaultRetryPolicy;
  }

  public void setDefaultRetryPolicy(Boolean defaultRetryPolicy) {
    this.defaultRetryPolicy = defaultRetryPolicy;
  }
}
