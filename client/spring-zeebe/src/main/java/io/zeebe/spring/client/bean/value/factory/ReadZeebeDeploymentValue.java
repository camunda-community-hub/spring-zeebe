package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.zeebe.spring.util.ZeebeExpressionResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReadZeebeDeploymentValue
  extends ReadAnnotationValue<ClassInfo, ZeebeDeployment, ZeebeDeploymentValue> {

  public ReadZeebeDeploymentValue(final ZeebeExpressionResolver resolver) {
    super(resolver, ZeebeDeployment.class);
  }

  @Override
  public Optional<ZeebeDeploymentValue> apply(final ClassInfo classInfo) {
    return classInfo
      .getAnnotation(annotationType)
      .map(
        annotation ->
          ZeebeDeploymentValue.builder()
            .beanInfo(classInfo)
            .classPathResources(
              resolveResources(annotation.classPathResources())
            )
            .build());
  }

  private List<String> resolveResources(String[] classPathResources) {
    return Arrays.stream(classPathResources)
      .map(resource -> ((String) resolver.resolve(resource)))
      .collect(Collectors.toList());
  }
}
