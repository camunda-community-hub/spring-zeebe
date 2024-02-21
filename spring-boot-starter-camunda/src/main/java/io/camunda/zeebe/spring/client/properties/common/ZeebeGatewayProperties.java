package io.camunda.zeebe.spring.client.properties.common;

import java.time.Duration;

public class ZeebeGatewayProperties extends ApiProperties {
  private Integer executionThreads;
  private Integer maxJobsActive;
  private String jobWorkerName;
  private Duration jobTimeout;
  private Duration jobPollInterval;
  private Duration messageTimeToLive;
  private Duration requestTimeout;
  private String caCertificatePath;
  private Duration keepAlive;
  private String overrideAuthority;


  public Integer getExecutionThreads() {
    return executionThreads;
  }

  public void setExecutionThreads(Integer executionThreads) {
    this.executionThreads = executionThreads;
  }

  public Integer getMaxJobsActive() {
    return maxJobsActive;
  }

  public void setMaxJobsActive(Integer maxJobsActive) {
    this.maxJobsActive = maxJobsActive;
  }

  public String getJobWorkerName() {
    return jobWorkerName;
  }

  public void setJobWorkerName(String jobWorkerName) {
    this.jobWorkerName = jobWorkerName;
  }

  public Duration getJobTimeout() {
    return jobTimeout;
  }

  public void setJobTimeout(Duration jobTimeout) {
    this.jobTimeout = jobTimeout;
  }

  public Duration getJobPollInterval() {
    return jobPollInterval;
  }

  public void setJobPollInterval(Duration jobPollInterval) {
    this.jobPollInterval = jobPollInterval;
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
}
