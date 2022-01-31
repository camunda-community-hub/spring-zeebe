package io.camunda.zeebe.spring.client.bean.value;

import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ZeebeWorkerValue implements ZeebeAnnotationValue<MethodInfo> {

  private String type;

  private String name;

  private long timeout;

  private int maxJobsActive;

  private long requestTimeout;

  private long pollInterval;

  private boolean autoComplete;

  private String[] fetchVariables;

  private MethodInfo methodInfo;

  private ZeebeWorkerValue(String type, String name, long timeout, int maxJobsActive, long requestTimeout, long pollInterval, String[] fetchVariables, boolean forceFetchAllVariables, List<ParameterInfo> variableParameters, boolean autoComplete, MethodInfo methodInfo) {
    this.type = type;
    this.name = name;
    this.timeout = timeout;
    this.maxJobsActive = maxJobsActive;
    this.requestTimeout = requestTimeout;
    this.pollInterval = pollInterval;
    this.autoComplete = autoComplete;
    this.methodInfo = methodInfo;

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

  public long getTimeout() {
    return timeout;
  }

  public int getMaxJobsActive() {
    return maxJobsActive;
  }

  public long getRequestTimeout() {
    return requestTimeout;
  }

  public long getPollInterval() {
    return pollInterval;
  }

  public String[] getFetchVariables() {
    return fetchVariables;
  }

  public boolean isAutoComplete() {
    return autoComplete;
  }

  public MethodInfo getMethodInfo() {
    return methodInfo;
  }

  @Override
  public MethodInfo getBeanInfo() {
    return getMethodInfo();
  }

  public static final ZeebeWorkerValueBuilder builder() {
    return new ZeebeWorkerValueBuilder();
  }

  @Override
  public String toString() {
    return "ZeebeWorkerValue{" +
      "name='" + name + '\'' +
      ", type='" + type + '\'' +
      ", timeout=" + timeout +
      ", maxJobsActive=" + maxJobsActive +
      ", requestTimeout=" + requestTimeout +
      ", pollInterval=" + pollInterval +
      ", autoComplete=" + autoComplete +
      ", fetchVariables=" + Arrays.toString(fetchVariables) +
      ", methodInfo=" + methodInfo +
      '}';
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

    public ZeebeWorkerValue build() {
      return new ZeebeWorkerValue(type, name, timeout, maxJobsActive, requestTimeout, pollInterval, fetchVariables, forceFetchAllVariables, variableParameters, autoComplete, methodInfo);
    }

  }
}
