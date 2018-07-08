package io.zeebe.spring.client.bean;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@Builder
public class MethodInfo implements BeanInfo {

  private ClassInfo classInfo;
  private Method method;

  @Override
  public Object getBean() {
    return classInfo.getBean();
  }

  @Override
  public String getBeanName() {
    return classInfo.getBeanName();
  }

  @SneakyThrows
  public Object invoke(final Object... args) {
    return method.invoke(getBean(), args);
  }

  public <T extends Annotation> Optional<T> getAnnotation(final Class<T> type) {
    return Optional.ofNullable(findAnnotation(method, type));
  }
}
