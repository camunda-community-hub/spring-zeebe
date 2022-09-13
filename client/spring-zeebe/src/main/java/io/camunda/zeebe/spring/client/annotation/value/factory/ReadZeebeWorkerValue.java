package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;

import java.util.*;
import java.util.stream.Collectors;

public class ReadZeebeWorkerValue extends ReadAnnotationValue<MethodInfo, ZeebeWorker, ZeebeWorkerValue> {

  private final String defaultWorkerType;

  public ReadZeebeWorkerValue(String defaultWorkerType) {
    super(ZeebeWorker.class);
    this.defaultWorkerType = defaultWorkerType;
  }

  @Override
  public Optional<ZeebeWorkerValue> apply(final MethodInfo methodInfo) {
    return methodInfo
      .getAnnotation(annotationType)
      .map(
        annotation -> {
          ZeebeWorkerValue workerValue = new ZeebeWorkerValue()
            .setMethodInfo(methodInfo)
            .setType(annotation.type())
            .setTimeout(annotation.timeout())
            .setMaxJobsActive(annotation.maxJobsActive())
            .setPollInterval(annotation.pollInterval())
            .setAutoComplete(annotation.autoComplete())
            .setRequestTimeout(annotation.requestTimeout())
            .setEnabled(annotation.enabled());

          if (annotation.forceFetchAllVariables()) {
            // this overwrites any other setting
            workerValue.setFetchVariables(new String[0]);
          } else {
            // make sure variables configured and annotated parameters are both fetched, use a set to avoid duplicates
            Set<String> variables = new HashSet<>();
            variables.addAll(Arrays.asList(annotation.fetchVariables()));
            variables.addAll(readZeebeVariableParameters(methodInfo).stream().map(ParameterInfo::getParameterName).collect(Collectors.toList()));
            workerValue.setFetchVariables(variables.toArray(new String[0]));
          }

          // Set name only if configured, otherwise default from Java Client library is used
          String name = annotation.name();
          if (name != null && name.length() > 0) {
            workerValue.setName(name);
          }

          String jobType = annotation.type();
          if (jobType!=null && jobType.length() > 0) {
            workerValue.setType(jobType);
          } else if (defaultWorkerType!=null) {
            workerValue.setType(defaultWorkerType);
          } else {
            workerValue.setType( methodInfo.getMethodName() );
          }

          return workerValue;
        });
  }

  private List<ParameterInfo> readZeebeVariableParameters(MethodInfo methodInfo) {
    return methodInfo.getParametersFilteredByAnnotation(ZeebeVariable.class);
  }
}
