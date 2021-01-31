package io.zeebe.spring.client.bean;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class MethodInfo implements BeanInfo {

  private ClassInfo classInfo;
  private Method method;

  private MethodInfo(ClassInfo classInfo, Method method) {
    this.classInfo = classInfo;
    this.method = method;
  }

  @Override
  public Object getBean() {
    return classInfo.getBean();
  }

  @Override
  public String getBeanName() {
    return classInfo.getBeanName();
  }

  public Object invoke(final Object... args) {
    try {
      return method.invoke(getBean(), args);
    } catch (InvocationTargetException e) {
      final Throwable targetException = e.getTargetException();
      if (targetException instanceof RuntimeException) {
        throw (RuntimeException) targetException;
      }
      else {
        throw new RuntimeException("Failed to invoke method: " + method.getName(), targetException);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to invoke method: " + method.getName(), e);
    }
  }

  public <T extends Annotation> Optional<T> getAnnotation(final Class<T> type) {
    return Optional.ofNullable(findAnnotation(method, type));
  }

  public static MethodInfoBuilder builder() {
    return new MethodInfoBuilder();
  }

  public static final class MethodInfoBuilder {

    private ClassInfo classInfo;
    private Method method;

    private MethodInfoBuilder() {
    }

    public MethodInfoBuilder classInfo(ClassInfo classInfo) {
      this.classInfo = classInfo;
      return this;
    }

    public MethodInfoBuilder method(Method method) {
      this.method = method;
      return this;
    }

    public MethodInfo build() {
      return new MethodInfo(classInfo, method);
    }
  }
}
