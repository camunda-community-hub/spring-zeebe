package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.zeebe.spring.util.ZeebeExpressionResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ReadZeebeDeploymentValueTest {

  @Mock
  private ZeebeExpressionResolver expressionResolver;

  private ReadZeebeDeploymentValue readZeebeDeploymentValue;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    readZeebeDeploymentValue = new ReadZeebeDeploymentValue(expressionResolver);
  }

  @Test
  public void shouldReadSingleClassPathResourceTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .bean(new WithSingleClassPathResource())
      .build();

    when(expressionResolver.resolve(anyString())).thenAnswer(inv -> inv.getArgument(0));

    ZeebeDeploymentValue expectedDeploymentValue = ZeebeDeploymentValue.builder()
      .beanInfo(classInfo)
      .classPathResources(Collections.singletonList("/1.bpmn"))
      .build();

    //when
    Optional<ZeebeDeploymentValue> valueForClass = readZeebeDeploymentValue.apply(classInfo);

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

    when(expressionResolver.resolve(anyString())).thenAnswer(inv -> inv.getArgument(0));

    ZeebeDeploymentValue expectedDeploymentValue = ZeebeDeploymentValue.builder()
      .beanInfo(classInfo)
      .classPathResources(Arrays.asList("/1.bpmn", "/2.bpmn"))
      .build();

    //when
    Optional<ZeebeDeploymentValue> valueForClass = readZeebeDeploymentValue.apply(classInfo);

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

    when(expressionResolver.resolve(anyString())).thenAnswer(inv -> inv.getArgument(0));

    //when
    Optional<ZeebeDeploymentValue> valueForClass = readZeebeDeploymentValue.apply(classInfo);

    //then
    assertFalse(valueForClass.isPresent());
  }

  @ZeebeDeployment(classPathResources = "/1.bpmn")
  private static class WithSingleClassPathResource {

  }

  @ZeebeDeployment(classPathResources = {"/1.bpmn", "/2.bpmn"})
  private static class WithMultipleClassPathResource {

  }

  private static class WithoutAnnotation {

  }
}
