package io.zeebe.spring.client.bean.value.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.ClassInfoTest;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.zeebe.spring.util.ZeebeExpressionResolver;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.Test;

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
    assertArrayEquals(new String[] {}, zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getBeanInfo());
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
    assertArrayEquals(new String[] { "foo"}, zeebeWorkerValue.get().getFetchVariables());
    assertEquals(methodInfo, zeebeWorkerValue.get().getBeanInfo());
  }

  private MethodInfo extract(Class<?> clazz) throws NoSuchMethodException {
    final Method method = clazz.getMethod("handle");;
    final ClassInfo classInfo = ClassInfo.builder()
      .build();
    return MethodInfo.builder()
      .classInfo(classInfo)
      .method(method)
      .build();
  }
}
