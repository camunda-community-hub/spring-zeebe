package io.zeebe.spring.client.bean;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeDeploymentValue implements ZeebeAnnotationValue<ClassInfo> {

    String topicName;

    String classPathResource;

    ClassInfo beanInfo;
}


