package io.zeebe.spring.client.bean;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeTaskListenerValue implements ZeebeAnnotationValue<MethodInfo> {

    private String topicName;

    private String taskType;

    private String lockOwner;

    private long lockTime;

    private int taskFetchSize;

    private MethodInfo beanInfo;
}

