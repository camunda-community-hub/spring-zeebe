package io.camunda.zeebe.spring.client.bean;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;

public class ClassInfoTest {

  @Deployment(classPathResources = "/1.bpmn")
  public static class WithDeploymentAnnotation {

  }

  public static class WithoutDeploymentAnnotation {

  }

  public static class WithZeebeWorker {

    @ZeebeWorker(type = "bar", timeout = 100L, name = "kermit")
    public void handle() {
    }
  }

  public static class WithZeebeWorkerAllValues {

    @ZeebeWorker(type = "bar", timeout = 100L, name = "kermit", requestTimeout = 500L, pollInterval = 1_000L, maxJobsActive = 3, fetchVariables = { "foo"}, autoComplete = true, enabled = true)
    public void handle() {
    }
  }

  public static class WithZeebeWorkerVariables {
    @ZeebeWorker(type = "bar", timeout = 100L, fetchVariables = "var3")
    public void handle(@Variable String var1, @Variable int var2) {
    }
  }

  public static class WithDisabledZeebeWorker {
    @ZeebeWorker(type = "bar", enabled = false)
    public void handle(@Variable String var1, @Variable int var2) {
    }
  }

  public static class NoPropertiesSet {
    @ZeebeWorker
    public void handle() {
    }
  }

  @Test
  public void getBeanInfo() throws Exception {
    final WithDeploymentAnnotation withDeploymentAnnotation = new WithDeploymentAnnotation();

    final ClassInfo beanInfo = beanInfo(withDeploymentAnnotation);

    assertThat(beanInfo.getBean()).isEqualTo(withDeploymentAnnotation);
    assertThat(beanInfo.getBeanName()).isEqualTo("withDeploymentAnnotation");
    assertThat(beanInfo.getTargetClass()).isEqualTo(WithDeploymentAnnotation.class);
  }

  @Test
  public void hasZeebeeDeploymentAnnotation() throws Exception {
    assertThat(beanInfo(new WithDeploymentAnnotation()).hasClassAnnotation(Deployment.class))
      .isTrue();
  }

  @Test
  public void hasNoZeebeeDeploymentAnnotation() throws Exception {
    assertThat(
      beanInfo(new WithoutDeploymentAnnotation()).hasClassAnnotation(Deployment.class))
      .isFalse();
  }

  @Test
  public void hasZeebeWorkerMethod() throws Exception {
    assertThat(beanInfo(new WithZeebeWorker()).hasMethodAnnotation(ZeebeWorker.class)).isTrue();
  }

  @Test
  public void hasNotZeebeWorkerMethod() throws Exception {
    assertThat(beanInfo("normal String").hasMethodAnnotation(ZeebeWorker.class)).isFalse();
  }

  private ClassInfo beanInfo(final Object bean) {
    return ClassInfo.builder().bean(bean).beanName(Introspector.decapitalize(bean.getClass().getSimpleName())).build();
  }
}
