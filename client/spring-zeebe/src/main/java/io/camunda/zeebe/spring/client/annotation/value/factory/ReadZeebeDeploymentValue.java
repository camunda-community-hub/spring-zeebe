package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeDeploymentValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReadZeebeDeploymentValue extends ReadAnnotationValue<ClassInfo, Deployment, ZeebeDeploymentValue> {

  private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

  public ReadZeebeDeploymentValue() {
    super(Deployment.class);
  }

  @Override
  public Optional<ZeebeDeploymentValue> apply(final ClassInfo classInfo) {
    return classInfo
      .getAnnotation(annotationType)
      .map(
        annotation -> {
          List<String> resources = Arrays.stream(annotation.resources()).collect(Collectors.toList());

          String[] classPathResources = annotation.classPathResources();
          if (classPathResources.length > 0) {
            resources.addAll(
              Arrays.stream(classPathResources)
              .map(resource -> CLASSPATH_ALL_URL_PREFIX + resource)
              .collect(Collectors.toList())
            );
          }

          return ZeebeDeploymentValue.builder()
            .beanInfo(classInfo)
            .resources(resources)
            .build();
        });
  }

}
