package io.camunda.zeebe.spring.client.annotation.value;

import io.camunda.zeebe.spring.client.bean.CopyNotNullBeanUtilsBean;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ZeebeWorkerValue implements ZeebeAnnotationValue<MethodInfo> {

  private static final CopyNotNullBeanUtilsBean BEAN_UTILS_BEAN = new CopyNotNullBeanUtilsBean();
  private String type;

  private String name;

  private Long timeout;

  private Integer maxJobsActive;

  private Long requestTimeout;

  private Long pollInterval;

  private Boolean autoComplete;

  private String[] fetchVariables;

  private Boolean enabled;

  private MethodInfo methodInfo;

  public ZeebeWorkerValue() {
  }

  private ZeebeWorkerValue(String type,
                           String name,
                           long timeout,
                           int maxJobsActive,
                           long requestTimeout,
                           long pollInterval,
                           String[] fetchVariables,
                           boolean forceFetchAllVariables,
                           List<ParameterInfo> variableParameters,
                           boolean autoComplete,
                           MethodInfo methodInfo,
                           final boolean enabled) {
    this.type = type;
    this.name = name;
    this.timeout = timeout;
    this.maxJobsActive = maxJobsActive;
    this.requestTimeout = requestTimeout;
    this.pollInterval = pollInterval;
    this.autoComplete = autoComplete;
    this.methodInfo = methodInfo;
    this.enabled = enabled;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public Long getTimeout() {
    return timeout;
  }

  public Integer getMaxJobsActive() {
    return maxJobsActive;
  }

  public Long getRequestTimeout() {
    return requestTimeout;
  }

  public Long getPollInterval() {
    return pollInterval;
  }

  public String[] getFetchVariables() {
    return fetchVariables;
  }

  public Boolean getAutoComplete() {
    return autoComplete;
  }

  public MethodInfo getMethodInfo() {
    return methodInfo;
  }

  @Override
  public MethodInfo getBeanInfo() {
    return getMethodInfo();
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public ZeebeWorkerValue setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public ZeebeWorkerValue setType(String type) {
    this.type = type;
    return this;
  }

  public ZeebeWorkerValue setName(String name) {
    this.name = name;
    return this;
  }

  public ZeebeWorkerValue setTimeout(Long timeout) {
    this.timeout = timeout;
    return this;
  }

  public ZeebeWorkerValue setMaxJobsActive(Integer maxJobsActive) {
    this.maxJobsActive = maxJobsActive;
    return this;
  }

  public ZeebeWorkerValue setRequestTimeout(Long requestTimeout) {
    this.requestTimeout = requestTimeout;
    return this;
  }

  public ZeebeWorkerValue setPollInterval(Long pollInterval) {
    this.pollInterval = pollInterval;
    return this;
  }

  public ZeebeWorkerValue setAutoComplete(Boolean autoComplete) {
    this.autoComplete = autoComplete;
    return this;
  }

  public ZeebeWorkerValue setFetchVariables(String[] fetchVariables) {
    this.fetchVariables = fetchVariables;
    return this;
  }

  public ZeebeWorkerValue setMethodInfo(MethodInfo methodInfo) {
    this.methodInfo = methodInfo;
    return this;
  }

  public ZeebeWorkerValue merge(ZeebeWorkerValue other) {
    try {
      BEAN_UTILS_BEAN.copyProperties(this, other);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  @Override
  public String toString() {
    return "ZeebeWorkerValue{" +
      "type='" + type + '\'' +
      ", name='" + name + '\'' +
      ", timeout=" + timeout +
      ", maxJobsActive=" + maxJobsActive +
      ", requestTimeout=" + requestTimeout +
      ", pollInterval=" + pollInterval +
      ", autoComplete=" + autoComplete +
      ", fetchVariables=" + Arrays.toString(fetchVariables) +
      ", enabled=" + enabled +
      ", methodInfo=" + methodInfo +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ZeebeWorkerValue that = (ZeebeWorkerValue) o;
    return Objects.equals(type, that.type) &&
      Objects.equals(name, that.name) &&
      Objects.equals(timeout, that.timeout) &&
      Objects.equals(maxJobsActive, that.maxJobsActive) &&
      Objects.equals(requestTimeout, that.requestTimeout) &&
      Objects.equals(pollInterval, that.pollInterval) &&
      Objects.equals(autoComplete, that.autoComplete) &&
      Arrays.equals(fetchVariables, that.fetchVariables) &&
      Objects.equals(enabled, that.enabled) &&
      Objects.equals(methodInfo, that.methodInfo);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(type, name, timeout, maxJobsActive, requestTimeout, pollInterval, autoComplete, enabled, methodInfo);
    result = 31 * result + Arrays.hashCode(fetchVariables);
    return result;
  }

}
