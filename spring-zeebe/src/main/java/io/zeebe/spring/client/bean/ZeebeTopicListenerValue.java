package io.zeebe.spring.client.bean;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeTopicListenerValue implements ZeebeAnnotationValue<MethodInfo> {

    private String name;

    private String topic;

    private MethodInfo beanInfo;

}
