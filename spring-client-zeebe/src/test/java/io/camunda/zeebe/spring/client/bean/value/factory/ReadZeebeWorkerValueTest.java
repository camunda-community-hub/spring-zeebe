package io.camunda.zeebe.spring.client.bean.value.factory;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.camunda.zeebe.spring.client.annotation.processor.ZeebeWorkerAnnotationProcessor;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.ClassInfoTest;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ReadZeebeWorkerValueTest {

  public static final String DEFAULT_WORKER_TYPE = "defaultWorkerType";
  public static final String DEFAULT_WORKER_NAME = "defaultJobWorkerName";

  @Test
  public void applyOnWithZeebeWorker() {
    // given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorker.class);

    // when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue =
        annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    // then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals("bar", zeebeWorkerValue.get().getType());
    assertEquals("kermit", zeebeWorkerValue.get().getName());
    assertEquals(Duration.ofMillis(100), zeebeWorkerValue.get().getTimeout());
    assertEquals(-1, zeebeWorkerValue.get().getMaxJobsActive());
    assertEquals(Duration.ofSeconds(-1), zeebeWorkerValue.get().getRequestTimeout());
    assertEquals(Duration.ofMillis(-1), zeebeWorkerValue.get().getPollInterval());
    assertEquals(false, zeebeWorkerValue.get().getAutoComplete());
    assertEquals(List.of(), zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  @Test
  void shouldReadTenantIds() {
    // given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.TenantBound.class);

    // when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue =
        annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    // then
    assertTrue(zeebeWorkerValue.isPresent());
    assertThat(zeebeWorkerValue.get().getTenantIds()).containsOnly("tenant-1");
  }

  @Test
  public void applyOnWithZeebeWorkerAllValues() {
    // given
    final ZeebeWorkerAnnotationProcessor annotationProcessor = createDefaultAnnotationProcessor();
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorkerAllValues.class);

    // when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue =
        annotationProcessor.readJobWorkerAnnotationForMethod(methodInfo);

    // then
    assertTrue(zeebeWorkerValue.isPresent());
    assertEquals("bar", zeebeWorkerValue.get().getType());
    assertEquals("kermit", zeebeWorkerValue.get().getName());
    assertEquals(Duration.ofMillis(100L), zeebeWorkerValue.get().getTimeout());
    assertEquals(3, zeebeWorkerValue.get().getMaxJobsActive());
    assertEquals(Duration.ofSeconds(500L), zeebeWorkerValue.get().getRequestTimeout());
    assertEquals(Duration.ofSeconds(1L), zeebeWorkerValue.get().getPollInterval());
    assertEquals(true, zeebeWorkerValue.get().getAutoComplete());
    assertEquals(List.of("foo"), zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  private ZeebeWorkerAnnotationProcessor createDefaultAnnotationProcessor() {
    return new ZeebeWorkerAnnotationProcessor(null, new ArrayList<>());
  }

  private MethodInfo extract(Class<?> clazz) {

    final Method method =
        Arrays.stream(clazz.getMethods())
            .filter(m -> m.getName().equals("handle"))
            .findFirst()
            .get();
    final ClassInfo classInfo = ClassInfo.builder().build();
    return MethodInfo.builder().classInfo(classInfo).method(method).build();
  }
}
