package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.MethodInfo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeTopicListenerValue implements ZeebeAnnotationValue<MethodInfo> {

    private String name;

    private String topic;

    private MethodInfo beanInfo;

}
