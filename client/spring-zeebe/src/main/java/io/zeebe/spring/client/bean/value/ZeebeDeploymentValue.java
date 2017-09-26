package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.ClassInfo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZeebeDeploymentValue implements ZeebeAnnotationValue<ClassInfo> {

    private String topicName;

    private String classPathResource;

    private ClassInfo beanInfo;
}


