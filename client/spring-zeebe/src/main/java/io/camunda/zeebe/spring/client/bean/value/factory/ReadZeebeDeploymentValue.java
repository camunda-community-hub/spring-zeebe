package io.camunda.zeebe.spring.client.bean.value.factory;

import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.camunda.zeebe.spring.util.ZeebeExpressionResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReadZeebeDeploymentValue
  extends ReadAnnotationValue<ClassInfo, ZeebeDeployment, ZeebeDeploymentValue> {

  private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

  public ReadZeebeDeploymentValue(final ZeebeExpressionResolver resolver) {
    super(resolver, ZeebeDeployment.class);
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
            .resources(
              resolveResources(resources)
            )
            .build();
        });
  }

  private List<String> resolveResources(List<String> resources) {
    return resources.stream()
      .map(resource -> ((String) resolver.resolve(resource)))
      .collect(Collectors.toList());
  }
}
