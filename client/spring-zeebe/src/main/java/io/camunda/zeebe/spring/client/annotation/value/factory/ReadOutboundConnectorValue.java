package io.camunda.zeebe.spring.client.annotation.value.factory;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.value.OutboundConnectorValue;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.BeanInfo;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;

import java.util.Optional;

public class ReadOutboundConnectorValue extends ReadAnnotationValue<ClassInfo, OutboundConnector, OutboundConnectorValue> {

  public ReadOutboundConnectorValue() {
    super(OutboundConnector.class);
  }

  @Override
  public Optional<OutboundConnectorValue> apply(final ClassInfo classInfo) {
    return classInfo
      .getAnnotation(annotationType)
      .map(
        annotation -> {
          return new OutboundConnectorValue()
            .setBeanInfo(classInfo)
            .setType(annotation.type())
            .setName(annotation.name())
            .setInputVariables(annotation.inputVariables());
        });
  }


}
