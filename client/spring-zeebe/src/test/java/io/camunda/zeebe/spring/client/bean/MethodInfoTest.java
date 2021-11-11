package io.camunda.zeebe.spring.client.bean;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodInfoTest {

  public static class WithZeebeWorker {

    @ZeebeWorker(type = "bar", timeout = 100L, name = "kermit")
    public void handle() {
    }
  }

  public static class WithZeebeWorkerParamValues {

    @ZeebeWorker(type = "foo", name = "kermit")
    public void handle(@ZeebeVariable String foo, @ZeebeVariable("bar") String bar) {
    }
  }

  @Test
  public void applyOnWithZeebeWorker() throws Exception {
    final WithZeebeWorker withZeebeWorker = new WithZeebeWorker();
    Method method = withZeebeWorker.getClass().getMethod("handle");
    final MethodInfo methodInfo = extract(withZeebeWorker, method);

    assertThat(methodInfo.getBean()).isEqualTo(withZeebeWorker);
    assertThat(methodInfo.getFetchVariables()).isEqualTo(null);
  }

  @Test
  public void applyOnWithZeebeWorkerValues() throws Exception {
    final Set<String> expectKeySet = Sets.newSet("foo", "bar");
    final WithZeebeWorkerParamValues withZeebeWorkerValues = new WithZeebeWorkerParamValues();

    Method method = withZeebeWorkerValues.getClass().getMethod("handle", String.class, String.class);
    final MethodInfo methodInfo = extract(withZeebeWorkerValues, method);

    assertThat(methodInfo.getBean()).isEqualTo(withZeebeWorkerValues);
    assertThat(methodInfo.getFetchVariables().keySet()).isEqualTo(expectKeySet);
  }

  private static MethodInfo extract(final Object bean, Method method) {

    final ClassInfo classInfo = ClassInfo.builder()
      .bean(bean)
      .beanName(Introspector.decapitalize(bean.getClass().getSimpleName()))
      .build();

    Map<String, Class<?>> fetchVariables = classInfo.toMethodInfo(method).getFetchVariables();

    return MethodInfo.builder()
      .classInfo(classInfo)
      .method(method)
      .fetchVariables(fetchVariables)
      .build();
  }
}
