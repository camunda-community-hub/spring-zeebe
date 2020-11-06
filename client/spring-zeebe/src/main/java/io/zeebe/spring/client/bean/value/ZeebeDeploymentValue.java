package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.ClassInfo;
import java.util.List;
import java.util.Objects;

public class ZeebeDeploymentValue implements ZeebeAnnotationValue<ClassInfo> {

  private List<String> classPathResources;

  private ClassInfo beanInfo;

  private ZeebeDeploymentValue(List<String> classPathResources, ClassInfo beanInfo) {
    this.classPathResources = classPathResources;
    this.beanInfo = beanInfo;
  }

  public List<String> getClassPathResources() {
    return classPathResources;
  }

  @Override
  public ClassInfo getBeanInfo() {
    return beanInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ZeebeDeploymentValue that = (ZeebeDeploymentValue) o;
    return Objects.equals(classPathResources, that.classPathResources) &&
      Objects.equals(beanInfo, that.beanInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classPathResources, beanInfo);
  }

  @Override
  public String toString() {
    return "ZeebeDeploymentValue{" +
      "classPathResources=" + classPathResources +
      ", beanInfo=" + beanInfo +
      '}';
  }

  public static ZeebeDeploymentValueBuilder builder() {
    return new ZeebeDeploymentValueBuilder();
  }

  public static final class ZeebeDeploymentValueBuilder {

    private List<String> classPathResources;
    private ClassInfo beanInfo;

    private ZeebeDeploymentValueBuilder() {
    }

    public ZeebeDeploymentValueBuilder classPathResources(List<String> classPathResources) {
      this.classPathResources = classPathResources;
      return this;
    }

    public ZeebeDeploymentValueBuilder beanInfo(ClassInfo beanInfo) {
      this.beanInfo = beanInfo;
      return this;
    }

    public ZeebeDeploymentValue build() {
      return new ZeebeDeploymentValue(classPathResources, beanInfo);
    }
  }
}
