package io.camunda.zeebe.spring.client.bean.value.factory;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeDeploymentAnnotationProcessor;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeDeploymentValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReadZeebeDeploymentValueTest {

  private ZeebeDeploymentAnnotationProcessor annotationProcessor;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    annotationProcessor = new ZeebeDeploymentAnnotationProcessor();
  }

  @Test
  public void shouldReadSingleClassPathResourceTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .bean(new WithSingleClassPathResource())
      .build();

    ZeebeDeploymentValue expectedDeploymentValue = ZeebeDeploymentValue.builder()
      .beanInfo(classInfo)
      .resources(Collections.singletonList("classpath*:/1.bpmn"))
      .build();

    //when
    Optional<ZeebeDeploymentValue> valueForClass = annotationProcessor.readAnnotation(classInfo);

    //then
    assertTrue(valueForClass.isPresent());
    assertEquals(expectedDeploymentValue, valueForClass.get());
  }

  @Test
  public void shouldReadMultipleClassPathResourcesTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .bean(new WithMultipleClassPathResource())
      .build();

    ZeebeDeploymentValue expectedDeploymentValue = ZeebeDeploymentValue.builder()
      .beanInfo(classInfo)
      .resources(Arrays.asList("classpath*:/1.bpmn", "classpath*:/2.bpmn"))
      .build();

    //when
    Optional<ZeebeDeploymentValue> valueForClass = annotationProcessor.readAnnotation(classInfo);

    //then
    assertTrue(valueForClass.isPresent());
    assertEquals(expectedDeploymentValue, valueForClass.get());
  }

  @Test
  public void shouldReadNoClassPathResourcesTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .bean(new WithoutAnnotation())
      .build();

    //when
    Optional<ZeebeDeploymentValue> valueForClass = annotationProcessor.readAnnotation(classInfo);

    //then
    assertFalse(valueForClass.isPresent());
  }

  @Test
  public void shouldReadDeprecatedClassPathResourceTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .bean(new WithDeprecatedPathResource())
      .build();

    ZeebeDeploymentValue expectedDeploymentValue = ZeebeDeploymentValue.builder()
      .beanInfo(classInfo)
      .resources(Collections.singletonList("classpath*:/1.bpmn"))
      .build();

    //when
    Optional<ZeebeDeploymentValue> valueForClass = annotationProcessor.readAnnotation(classInfo);

    //then
    assertTrue(valueForClass.isPresent());
    assertEquals(expectedDeploymentValue, valueForClass.get());
  }

  @Deployment(resources = "classpath*:/1.bpmn")
  private static class WithSingleClassPathResource {

  }

  @ZeebeDeployment(classPathResources = "/1.bpmn")
  private static class WithDeprecatedPathResource {

  }

  @Deployment(resources = {"classpath*:/1.bpmn", "classpath*:/2.bpmn"})
  private static class WithMultipleClassPathResource {

  }

  private static class WithoutAnnotation {

  }
}
