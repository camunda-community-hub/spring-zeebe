package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.MethodInfo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeWorkerValue implements ZeebeAnnotationValue<MethodInfo> {

  private String topicName;

  private String jobType;

  private String lockOwner;

  private long lockTime;

  private int jobFetchSize;

  private MethodInfo beanInfo;
}
