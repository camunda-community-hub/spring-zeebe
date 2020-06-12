package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.MethodInfo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeWorkerValue implements ZeebeAnnotationValue<MethodInfo> {

  private String type;

  private String name;

  private long timeout;

  private int maxJobsActive;

  private long requestTimeout;

  private long pollInterval;

  private String[] fetchVariables;

  private MethodInfo beanInfo;
}
