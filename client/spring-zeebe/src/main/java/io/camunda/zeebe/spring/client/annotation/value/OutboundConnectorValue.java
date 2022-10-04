package io.camunda.zeebe.spring.client.annotation.value;

import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OutboundConnectorValue implements ZeebeAnnotationValue<ClassInfo> {

  private String name;
  private String type;
  private String[] inputVariables;
  private ClassInfo classInfo;

  public String getName() {
    return name;
  }

  public OutboundConnectorValue setName(String name) {
    this.name = name;
    return this;
  }

  public String getType() {
    return type;
  }

  public OutboundConnectorValue setType(String type) {
    this.type = type;
    return this;
  }

  public String[] getInputVariables() {
    return inputVariables;
  }

  public OutboundConnectorValue setInputVariables(String[] inputVariables) {
    this.inputVariables = inputVariables;
    return this;
  }

  public OutboundConnectorValue setBeanInfo(ClassInfo classInfo) {
    this.classInfo = classInfo;
    return this;
  }

  @Override
  public ClassInfo getBeanInfo() {
    return classInfo;
  }

  @Override
  public String toString() {
    return "OutboundConnectorValue{" +
      "name='" + name + '\'' +
      ", type='" + type + '\'' +
      ", inputVariables=" + Arrays.toString(inputVariables) +
      ", classInfo=" + classInfo +
      '}';
  }
}
