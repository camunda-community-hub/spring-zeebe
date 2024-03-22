package io.camunda.zeebe.spring.client.properties;

import static java.util.Arrays.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

public class DeprecatedPropertiesTest {
  private static final List<String> IRRELEVANT_METHODS =
      Arrays.asList("toString", "equals", "hashCode");

  @TestFactory
  Stream<DynamicContainer> shouldDeprecate() {
    return ReflectionSupport.findAllClassesInPackage(
            "io.camunda.zeebe.spring.client.properties",
            c ->
                c.isAnnotationPresent(Deprecated.class)
                    && c.isAnnotationPresent(ConfigurationProperties.class),
            n -> true)
        .stream()
        .flatMap(this::unwrap)
        .map(
            c ->
                DynamicContainer.dynamicContainer(
                    c.getName(),
                    ReflectionSupport.findMethods(
                            c, this::isRelevant, HierarchyTraversalMode.TOP_DOWN)
                        .stream()
                        .map(
                            m ->
                                DynamicTest.dynamicTest(
                                    m.getName() + "()", () -> shouldBeDeprecatedProperty(m)))));
  }

  private boolean isRelevant(Method method) {
    if (method.getParameterCount() > 0) {
      return false;
    }
    if (IRRELEVANT_METHODS.contains(method.getName())) {
      return false;
    }
    if (Modifier.isPrivate(method.getModifiers())) {
      return false;
    }
    return true;
  }

  private Stream<Class<?>> unwrap(Class<?> clazz) {
    List<Class<?>> list =
        new java.util.ArrayList<>(
            stream(clazz.getClasses()).flatMap(this::unwrap).collect(Collectors.toList()));
    list.add(clazz);
    return list.stream();
  }

  private void shouldBeDeprecatedProperty(Method getter) {
    assertThat(getter)
        .matches(
            g -> g.isAnnotationPresent(DeprecatedConfigurationProperty.class),
            "Getter is annotated with spring deprecation annotation");
    DeprecatedConfigurationProperty annotation =
        getter.getAnnotation(DeprecatedConfigurationProperty.class);
    assertThat(annotation)
        .matches(a -> isNotEmpty(a.replacement()), "There is a replacement mentioned");
    assertThat(getter)
        .matches(
            g -> g.isAnnotationPresent(Deprecated.class),
            "Getter is annotated with deprecation annotation");
  }
}
