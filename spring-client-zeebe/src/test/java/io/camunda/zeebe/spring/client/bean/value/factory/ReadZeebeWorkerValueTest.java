package io.camunda.zeebe.spring.client.bean.value.factory;

import io.camunda.zeebe.spring.client.annotation.processor.ZeebeWorkerAnnotationProcessor;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.ClassInfoTest;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadZeebeWorkerValueTest {

  public static final String DEFAULT_WORKER_TYPE = "defaultWorkerType";
  public static final String DEFAULT_WORKER_NAME = "defaultJobWorkerName";

  @Test
  public void applyOnWithZeebeWorker() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorker.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals("bar", zeebeWorkerValue.get().getType());
    assertEquals("kermit", zeebeWorkerValue.get().getName());
    assertEquals(100L, zeebeWorkerValue.get().getTimeout());
    assertEquals(-1, zeebeWorkerValue.get().getMaxJobsActive());
    assertEquals(-1, zeebeWorkerValue.get().getRequestTimeout());
    assertEquals(-1, zeebeWorkerValue.get().getPollInterval());
    assertEquals(false, zeebeWorkerValue.get().getAutoComplete());
    assertArrayEquals(new String[] {}, zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  @Test
  public void applyOnWithZeebeWorkerAllValues() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorkerAllValues.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals("bar", zeebeWorkerValue.get().getType());
    assertEquals("kermit", zeebeWorkerValue.get().getName());
    assertEquals(100L, zeebeWorkerValue.get().getTimeout());
    assertEquals(3, zeebeWorkerValue.get().getMaxJobsActive());
    assertEquals(500L, zeebeWorkerValue.get().getRequestTimeout());
    assertEquals(1_000L, zeebeWorkerValue.get().getPollInterval());
    assertEquals(true, zeebeWorkerValue.get().getAutoComplete());
    assertArrayEquals(new String[] { "foo"}, zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  @Test
  public void applyOnWithZeebeWorkerVariables() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorkerVariables.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertThat(Arrays.asList("var1", "var2", "var3" )).hasSameElementsAs(Arrays.asList(zeebeWorkerValue.get().getFetchVariables()));
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  @Test
  public void applyOnZeebeWorkerWithNoTypeSet() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = new ZeebeWorkerAnnotationProcessor(
      null,
      new ArrayList<>(),
      null,
      DEFAULT_WORKER_NAME);
    final MethodInfo methodInfo = extract(ClassInfoTest.NoPropertiesSet.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals("handle", zeebeWorkerValue.get().getType());
  }

  @Test
  // MAybe valuidate via own test class with @TestPropertySource(properties = {"zeebe.client.worker.default-type=defaultWorkerType"})
  public void applyOnZeebeWorkerWithNoTypeSetUsingDefault() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.NoPropertiesSet.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals(DEFAULT_WORKER_TYPE, zeebeWorkerValue.get().getType());
  }

  @Test
  public void applyOnZeebeWorkerWithNoNameSetUsingDefault() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.NoPropertiesSet.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals(DEFAULT_WORKER_NAME, zeebeWorkerValue.get().getName());
  }

  @Test
  public void applyOnZeebeWorkerWithNoNameNoDefault() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = new ZeebeWorkerAnnotationProcessor(
      null,
      new ArrayList<>(),
      DEFAULT_WORKER_TYPE,
      null);
    final MethodInfo methodInfo = extract(ClassInfoTest.NoPropertiesSet.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    // we are not using beans here - so beanName is null
    assertEquals("null#handle", zeebeWorkerValue.get().getName());
  }

  private ZeebeWorkerAnnotationProcessor createDefaultAnnotationProcessor() {
    return new ZeebeWorkerAnnotationProcessor(
      null,
      new ArrayList<>(),
      DEFAULT_WORKER_TYPE,
      DEFAULT_WORKER_NAME);
  }

  @Test
  public void applyOnWithZeebeWorkerVariablesComplexType() {
    //given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorkerVariablesComplexType.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertThat(Arrays.asList("var1", "var2")).hasSameElementsAs(Arrays.asList(zeebeWorkerValue.get().getFetchVariables()));
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  private MethodInfo extract(Class<?> clazz) {

    final Method method = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals("handle")).findFirst().get();
    final ClassInfo classInfo = ClassInfo.builder()
      .build();
    return MethodInfo.builder()
      .classInfo(classInfo)
      .method(method)
      .build();
  }
}
