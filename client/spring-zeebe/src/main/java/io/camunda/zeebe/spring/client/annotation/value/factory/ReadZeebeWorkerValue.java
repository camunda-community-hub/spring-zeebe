package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.properties.ZeebeClientProperties;
import io.camunda.zeebe.spring.util.ZeebeExpressionResolver;

import java.util.List;
import java.util.Optional;

public class ReadZeebeWorkerValue extends ReadAnnotationValue<MethodInfo, ZeebeWorker, ZeebeWorkerValue> {

  private final ZeebeClientProperties zeebeClientProperties;

  public ReadZeebeWorkerValue(final ZeebeExpressionResolver resolver, final ZeebeClientProperties zeebeClientProperties) {
    super(resolver, ZeebeWorker.class);
    this.zeebeClientProperties = zeebeClientProperties;
  }

  @Override
  public Optional<ZeebeWorkerValue> apply(final MethodInfo methodInfo) {
    return methodInfo
      .getAnnotation(annotationType)
      .flatMap(
        annotation -> {
          final String type = resolver.resolve(annotation.type());
          if (Optional
              .ofNullable(zeebeClientProperties.getWorkersConfiguration().get(type))
              .map(ZeebeClientProperties.WorkerConfiguration::isEnabled).orElse(true) &&
              annotation.enabled()) {
            ZeebeWorkerValue.ZeebeWorkerValueBuilder builder = ZeebeWorkerValue.builder()
              .methodInfo(methodInfo)
              .type(type)
              .timeout(annotation.timeout())
              .maxJobsActive(annotation.maxJobsActive())
              .pollInterval(annotation.pollInterval())
              .fetchVariables(annotation.fetchVariables())
              .forceFetchAllVariables(annotation.forceFetchAllVariables())
              .autoComplete(annotation.autoComplete())
              .variableParameters(readZeebeVariableParameters(methodInfo))
              .requestTimeout(annotation.requestTimeout());

            String name = resolver.resolve(annotation.name());
            if (name != null && name.length() > 0) {
              builder.name(name);
            }

            return Optional.of(builder.build());
          } else {
            return Optional.empty();
          }
        });
  }

  private List<ParameterInfo> readZeebeVariableParameters(MethodInfo methodInfo) {
    return methodInfo.getParametersFilteredByAnnotation(ZeebeVariable.class);
  }
}
