package io.camunda.zeebe.spring.client.bean.value.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.ClassInfoTest;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.util.ZeebeExpressionResolver;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ReadZeebeWorkerValueTest {

  @Test
  public void applyOnWithZeebeWorker() throws NoSuchMethodException {
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
    assertEquals(false, zeebeWorkerValue.get().isAutoComplete());
    assertArrayEquals(new String[] {}, zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  @Test
  public void applyOnWithZeebeWorkerAllValues() throws NoSuchMethodException {
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
    assertEquals(true, zeebeWorkerValue.get().isAutoComplete());
    assertArrayEquals(new String[] { "foo"}, zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getMethodInfo());
  }

  @Test
  public void applyOnWithZeebeWorkerVariables() throws NoSuchMethodException {
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

  private MethodInfo extract(Class<?> clazz) throws NoSuchMethodException {

    final Method method = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals("handle")).findFirst().get();
    final ClassInfo classInfo = ClassInfo.builder()
      .build();
    return MethodInfo.builder()
      .classInfo(classInfo)
      .method(method)
      .build();
  }
}
