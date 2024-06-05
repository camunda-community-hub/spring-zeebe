package io.camunda.zeebe.spring.client.annotation.value;

import io.camunda.zeebe.spring.client.bean.MethodInfo;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ZeebeWorkerValue implements ZeebeAnnotationValue<MethodInfo> {
  private String type;
  private String name;
  private Duration timeout;
  private Integer maxJobsActive;
  private Duration requestTimeout;
  private Duration pollInterval;
  private Boolean autoComplete;
  private List<String> fetchVariables;
  private Boolean enabled;
  private MethodInfo methodInfo;
  private List<String> tenantIds;
  private Boolean forceFetchAllVariables;
  private Boolean streamEnabled;
  private Duration streamTimeout;

  public ZeebeWorkerValue() {}

  public ZeebeWorkerValue(
      String type,
      String name,
      Duration timeout,
      Integer maxJobsActive,
      Duration requestTimeout,
      Duration pollInterval,
      Boolean autoComplete,
      List<String> fetchVariables,
      Boolean enabled,
      MethodInfo methodInfo,
      List<String> tenantIds,
      Boolean forceFetchAllVariables,
      Boolean streamEnabled,
      Duration streamTimeout) {
    this.type = type;
    this.name = name;
    this.timeout = timeout;
    this.maxJobsActive = maxJobsActive;
    this.requestTimeout = requestTimeout;
    this.pollInterval = pollInterval;
    this.autoComplete = autoComplete;
    this.fetchVariables = fetchVariables;
    this.enabled = enabled;
    this.methodInfo = methodInfo;
    this.tenantIds = tenantIds;
    this.forceFetchAllVariables = forceFetchAllVariables;
    this.streamEnabled = streamEnabled;
    this.streamTimeout = streamTimeout;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(Duration timeout) {
    this.timeout = timeout;
  }

  public Integer getMaxJobsActive() {
    return maxJobsActive;
  }

  public void setMaxJobsActive(Integer maxJobsActive) {
    this.maxJobsActive = maxJobsActive;
  }

  public Duration getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public Duration getPollInterval() {
    return pollInterval;
  }

  public void setPollInterval(Duration pollInterval) {
    this.pollInterval = pollInterval;
  }

  public Boolean getAutoComplete() {
    return autoComplete;
  }

  public void setAutoComplete(Boolean autoComplete) {
    this.autoComplete = autoComplete;
  }

  public List<String> getFetchVariables() {
    return fetchVariables;
  }

  public void setFetchVariables(List<String> fetchVariables) {
    this.fetchVariables = fetchVariables;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public MethodInfo getMethodInfo() {
    return methodInfo;
  }

  public void setMethodInfo(MethodInfo methodInfo) {
    this.methodInfo = methodInfo;
  }

  public List<String> getTenantIds() {
    return tenantIds;
  }

  public void setTenantIds(List<String> tenantIds) {
    this.tenantIds = tenantIds;
  }

  public Boolean getForceFetchAllVariables() {
    return forceFetchAllVariables;
  }

  public void setForceFetchAllVariables(Boolean forceFetchAllVariables) {
    this.forceFetchAllVariables = forceFetchAllVariables;
  }

  public Boolean getStreamEnabled() {
    return streamEnabled;
  }

  public void setStreamEnabled(Boolean streamEnabled) {
    this.streamEnabled = streamEnabled;
  }

  public Duration getStreamTimeout() {
    return streamTimeout;
  }

  public void setStreamTimeout(Duration streamTimeout) {
    this.streamTimeout = streamTimeout;
  }

  @Override
  public MethodInfo getBeanInfo() {
    return methodInfo;
  }

  @Override
  public String toString() {
    return "ZeebeWorkerValue{"
        + "type='"
        + type
        + '\''
        + ", name='"
        + name
        + '\''
        + ", timeout="
        + timeout
        + ", maxJobsActive="
        + maxJobsActive
        + ", requestTimeout="
        + requestTimeout
        + ", pollInterval="
        + pollInterval
        + ", autoComplete="
        + autoComplete
        + ", fetchVariables="
        + fetchVariables
        + ", enabled="
        + enabled
        + ", methodInfo="
        + methodInfo
        + ", tenantIds="
        + tenantIds
        + ", forceFetchAllVariables="
        + forceFetchAllVariables
        + ", streamEnabled="
        + streamEnabled
        + ", streamTimeout="
        + streamTimeout
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ZeebeWorkerValue that = (ZeebeWorkerValue) o;
    return Objects.equals(type, that.type)
        && Objects.equals(name, that.name)
        && Objects.equals(timeout, that.timeout)
        && Objects.equals(maxJobsActive, that.maxJobsActive)
        && Objects.equals(requestTimeout, that.requestTimeout)
        && Objects.equals(pollInterval, that.pollInterval)
        && Objects.equals(autoComplete, that.autoComplete)
        && Objects.equals(fetchVariables, that.fetchVariables)
        && Objects.equals(enabled, that.enabled)
        && Objects.equals(methodInfo, that.methodInfo)
        && Objects.equals(tenantIds, that.tenantIds)
        && Objects.equals(forceFetchAllVariables, that.forceFetchAllVariables)
        && Objects.equals(streamEnabled, that.streamEnabled)
        && Objects.equals(streamTimeout, that.streamTimeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        type,
        name,
        timeout,
        maxJobsActive,
        requestTimeout,
        pollInterval,
        autoComplete,
        fetchVariables,
        enabled,
        methodInfo,
        tenantIds,
        forceFetchAllVariables,
        streamEnabled,
        streamTimeout);
  }

  // old methods

  @Deprecated
  public void setFetchVariables(String[] fetchVariables) {
    this.fetchVariables = Arrays.asList(fetchVariables);
  }

  /** set timeout in millis */
  @Deprecated
  public void setTimeout(Long timeout) {
    this.timeout = Duration.ofMillis(timeout);
  }
}
