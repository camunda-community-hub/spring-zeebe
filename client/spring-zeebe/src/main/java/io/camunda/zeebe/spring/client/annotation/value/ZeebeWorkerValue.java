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

    if (forceFetchAllVariables) {
      // this overwrites any other setting
      this.fetchVariables = new String[0];
    } else {
      // make sure variables configured and annotated parameters are both fetched, use a set to avoid duplicates
      Set<String> variables = new HashSet<>();
      variables.addAll(Arrays.asList(fetchVariables));
      variables.addAll(variableParameters.stream().map(ParameterInfo::getParameterName).collect(Collectors.toList()));
      this.fetchVariables = variables.toArray(new String[0]);
    }
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

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTimeout(Long timeout) {
    this.timeout = timeout;
  }

  public void setMaxJobsActive(Integer maxJobsActive) {
    this.maxJobsActive = maxJobsActive;
  }

  public void setRequestTimeout(Long requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public void setPollInterval(Long pollInterval) {
    this.pollInterval = pollInterval;
  }

  public void setAutoComplete(Boolean autoComplete) {
    this.autoComplete = autoComplete;
  }

  public void setFetchVariables(String[] fetchVariables) {
    this.fetchVariables = fetchVariables;
  }

  public void setMethodInfo(MethodInfo methodInfo) {
    this.methodInfo = methodInfo;
  }

  public ZeebeWorkerValue merge(ZeebeWorkerValue other) {
    try {
      BEAN_UTILS_BEAN.copyProperties(this, other);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public static final ZeebeWorkerValueBuilder builder() {
    return new ZeebeWorkerValueBuilder();
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

  public static final class ZeebeWorkerValueBuilder {

    private String type;

    private String name;

    private long timeout;

    private int maxJobsActive;

    private long requestTimeout;

    private long pollInterval;

    private String[] fetchVariables;

    private boolean forceFetchAllVariables;

    private List<ParameterInfo> variableParameters;

    private boolean autoComplete;

    private boolean enabled;

    private MethodInfo methodInfo;

    private ZeebeWorkerValueBuilder() {
    }

    public ZeebeWorkerValueBuilder type(String type) {
      this.type = type;
      return this;
    }

    public ZeebeWorkerValueBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ZeebeWorkerValueBuilder timeout(long timeout) {
      this.timeout = timeout;
      return this;
    }

    public ZeebeWorkerValueBuilder maxJobsActive(int maxJobsActive) {
      this.maxJobsActive = maxJobsActive;
      return this;
    }

    public ZeebeWorkerValueBuilder requestTimeout(long requestTimeout) {
      this.requestTimeout = requestTimeout;
      return this;
    }

    public ZeebeWorkerValueBuilder pollInterval(long pollInterval) {
      this.pollInterval = pollInterval;
      return this;
    }

    public ZeebeWorkerValueBuilder fetchVariables(String[] fetchVariables) {
      this.fetchVariables = fetchVariables;
      return this;
    }

    public ZeebeWorkerValueBuilder forceFetchAllVariables(boolean forceFetchAllVariables) {
      this.forceFetchAllVariables = forceFetchAllVariables;
      return this;
    }

    public ZeebeWorkerValueBuilder autoComplete(boolean autoComplete) {
      this.autoComplete = autoComplete;
      return this;
    }

    public ZeebeWorkerValueBuilder methodInfo(MethodInfo methodInfo) {
      this.methodInfo = methodInfo;
      return this;
    }

    public ZeebeWorkerValueBuilder variableParameters(List<ParameterInfo> variableParameters) {
      this.variableParameters = variableParameters;
      return this;
    }

    public ZeebeWorkerValueBuilder enabled(final boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public ZeebeWorkerValue build() {
      return new ZeebeWorkerValue(
        type,
        name,
        timeout,
        maxJobsActive,
        requestTimeout,
        pollInterval,
        fetchVariables,
        forceFetchAllVariables,
        variableParameters,
        autoComplete,
        methodInfo,
        enabled
      );
    }

  }
}
