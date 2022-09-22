package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.MethodInfo;

import java.util.*;

public class ReadZeebeWorkerValue extends ReadAnnotationValue<MethodInfo, ZeebeWorker, ZeebeWorkerValue> {

  private final String defaultWorkerType;
  private String defaultWorkerName;

  public ReadZeebeWorkerValue(String defaultWorkerType, String defaultJobWorkerName) {
    super(ZeebeWorker.class);
    this.defaultWorkerType = defaultWorkerType;
    this.defaultWorkerName = defaultJobWorkerName;
  }

  @Override
  public Optional<ZeebeWorkerValue> apply(final MethodInfo methodInfo) {
    return methodInfo
      .getAnnotation(annotationType)
      .map(
        annotation -> {
          return new ZeebeWorkerValue()
            .setMethodInfo(methodInfo)
            .setType(annotation.type())
            .setTimeout(annotation.timeout())
            .setMaxJobsActive(annotation.maxJobsActive())
            .setPollInterval(annotation.pollInterval())
            .setAutoComplete(annotation.autoComplete())
            .setRequestTimeout(annotation.requestTimeout())
            .setEnabled(annotation.enabled())

            // TODO Get rid of those initialize methods but add the attributes as values onto the worker and then auto-initialize stuff when opening the worker
            .initializeName(annotation.name(), methodInfo, defaultWorkerName)
            .initializeFetchVariables(annotation.forceFetchAllVariables(), annotation.fetchVariables(), methodInfo)
            .initializeJobType(annotation.type(), methodInfo, defaultWorkerType);
        });
  }


}
