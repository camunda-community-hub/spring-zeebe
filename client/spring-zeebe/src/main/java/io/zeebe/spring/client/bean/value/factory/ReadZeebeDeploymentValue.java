package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.zeebe.spring.client.util.ZeebeExpressionResolver;

import java.util.Optional;

public class ReadZeebeDeploymentValue extends ReadAnnotationValue<ClassInfo, ZeebeDeployment, ZeebeDeploymentValue> {

    public ReadZeebeDeploymentValue(final ZeebeExpressionResolver resolver) {
        super(resolver, ZeebeDeployment.class);
    }

    @Override
    public Optional<ZeebeDeploymentValue> apply(ClassInfo classInfo) {
        return classInfo.getAnnotation(annotationType)
                .map(annotation -> ZeebeDeploymentValue.builder()
                        .beanInfo(classInfo)
                        .topicName(resolver.resolve(annotation.topicName()))
                        .classPathResource(resolver.resolve(annotation.classPathResource()))
                        .build());
    }
}
