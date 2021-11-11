package io.camunda.zeebe.spring.client.bean;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ClassInfo implements BeanInfo {

  private final LocalVariableTableParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

  private Object bean;
  private String beanName;

  private ClassInfo(Object bean, String beanName) {
    this.bean = bean;
    this.beanName = beanName;
  }

  @Override
  public Object getBean() {
    return bean;
  }

  @Override
  public String getBeanName() {
    return beanName;
  }

  public MethodInfo toMethodInfo(final Method method) {

    MethodInfo.MethodInfoBuilder methodInfoBuilder = MethodInfo.builder().classInfo(this).method(method);

    Map<String, Class<?>> fetchVariables = null;
    Parameter[] parameters = method.getParameters();
    ZeebeWorker annotation = method.getAnnotation(ZeebeWorker.class);
    if (Objects.nonNull(annotation)) {
      int i = 0;
      String[] params = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
      for (Parameter parameter : parameters) {
        Class<?> parameterType = parameter.getType();
        ZeebeVariable zeebeVariable = parameter.getAnnotation(ZeebeVariable.class);
        if (Objects.nonNull(zeebeVariable)) {
          String variable = null;
          if (params != null && params.length > 0) {
            variable = params[i];
          }
          if (!zeebeVariable.value().isEmpty()) {
            variable = zeebeVariable.value();
          }
          if (fetchVariables == null) {
            fetchVariables = new LinkedHashMap<>();
          }
          fetchVariables.put(variable, parameterType);
        } else {
          if (JobClient.class.isAssignableFrom(parameterType)
            || ActivatedJob.class.isAssignableFrom(parameterType)) {

            if (fetchVariables == null) {
              fetchVariables = new LinkedHashMap<>();
            }

            fetchVariables.put(parameterType.getSimpleName(), parameterType);
          }
        }
        i++;
      }
      methodInfoBuilder.fetchVariables(fetchVariables);
    }

    return methodInfoBuilder.build();
  }

  public <T extends Annotation> Optional<T> getAnnotation(final Class<T> type) {
    return Optional.ofNullable(findAnnotation(getTargetClass(), type));
  }

  public static final ClassInfoBuilder builder() {
    return new ClassInfoBuilder();
  }

  public static final class ClassInfoBuilder {

    private Object bean;
    private String beanName;

    public ClassInfoBuilder() {
    }

    public ClassInfoBuilder bean(Object bean) {
      this.bean = bean;
      return this;
    }

    public ClassInfoBuilder beanName(String beanName) {
      this.beanName = beanName;
      return this;
    }

    public ClassInfo build() {
      return new ClassInfo(bean, beanName);
    }
  }
}
