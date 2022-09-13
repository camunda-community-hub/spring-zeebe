package io.camunda.zeebe.spring.client.bean.value.factory;

import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.ClassInfoTest;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.util.ZeebeExpressionResolver;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadZeebeWorkerValueTest {

  @Test
  public void applyOnWithZeebeWorker() {
    //given
    final ZeebeExpressionResolver zeebeExpressionResolver = new ZeebeExpressionResolver();
    final ReadZeebeWorkerValue readZeebeWorkerValue = new ReadZeebeWorkerValue(zeebeExpressionResolver);
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorker.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = readZeebeWorkerValue.apply(methodInfo);

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
    final ZeebeExpressionResolver zeebeExpressionResolver = new ZeebeExpressionResolver();
    final ReadZeebeWorkerValue readZeebeWorkerValue = new ReadZeebeWorkerValue(zeebeExpressionResolver);
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorkerAllValues.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = readZeebeWorkerValue.apply(methodInfo);

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
    final ZeebeExpressionResolver zeebeExpressionResolver = new ZeebeExpressionResolver();
    final ReadZeebeWorkerValue readZeebeWorkerValue = new ReadZeebeWorkerValue(zeebeExpressionResolver);
    final MethodInfo methodInfo = extract(ClassInfoTest.WithZeebeWorkerVariables.class);

    //when
    final Optional<ZeebeWorkerValue> zeebeWorkerValue = readZeebeWorkerValue.apply(methodInfo);

    //then
    assertTrue(zeebeWorkerValue.isPresent());
    assertThat(Arrays.asList("var1", "var2", "var3" )).hasSameElementsAs(Arrays.asList(zeebeWorkerValue.get().getFetchVariables()));
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
