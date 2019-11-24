package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.ClassInfo;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ZeebeDeploymentValue implements ZeebeAnnotationValue<ClassInfo> {

  private List<String> classPathResources;

  private ClassInfo beanInfo;
}
