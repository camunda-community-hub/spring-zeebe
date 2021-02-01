package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.MethodInfo;

public class ZeebeWorkerValue implements ZeebeAnnotationValue<MethodInfo> {

  private String type;

  private String name;

  private long timeout;

  private int maxJobsActive;

  private long requestTimeout;

  private long pollInterval;

  private String[] fetchVariables;

  private MethodInfo beanInfo;

  private ZeebeWorkerValue(String type, String name, long timeout, int maxJobsActive, long requestTimeout, long pollInterval, String[] fetchVariables, MethodInfo beanInfo) {
    this.type = type;
    this.name = name;
    this.timeout = timeout;
    this.maxJobsActive = maxJobsActive;
    this.requestTimeout = requestTimeout;
    this.pollInterval = pollInterval;
    this.fetchVariables = fetchVariables;
    this.beanInfo = beanInfo;
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

  @Override
  public MethodInfo getBeanInfo() {
    return beanInfo;
  }

  public static final ZeebeWorkerValueBuilder builder() {
    return new ZeebeWorkerValueBuilder();
  }

  public static final class ZeebeWorkerValueBuilder {

    private String type;

    private String name;

    private long timeout;

    private int maxJobsActive;

    private long requestTimeout;

    private long pollInterval;

    private String[] fetchVariables;

    private MethodInfo beanInfo;

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

    public ZeebeWorkerValueBuilder beanInfo(MethodInfo beanInfo) {
      this.beanInfo = beanInfo;
      return this;
    }

    public ZeebeWorkerValue build() {
      return new ZeebeWorkerValue(type, name, timeout, maxJobsActive, requestTimeout, pollInterval, fetchVariables, beanInfo);
    }
  }
}
