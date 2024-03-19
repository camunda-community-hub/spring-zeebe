package io.camunda.zeebe.spring.client.properties;

import static io.camunda.zeebe.spring.client.configuration.PropertyUtil.*;
import static io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties.*;
import static java.util.Optional.*;
import static org.apache.commons.lang3.StringUtils.*;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariablesAsType;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.CopyNotNullBeanUtilsBean;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.properties.common.ZeebeClientProperties;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class PropertyBasedZeebeWorkerValueCustomizer implements ZeebeWorkerValueCustomizer {
  private static final Logger LOG =
      LoggerFactory.getLogger(PropertyBasedZeebeWorkerValueCustomizer.class);
  private static final CopyNotNullBeanUtilsBean BEAN_UTILS_BEAN = new CopyNotNullBeanUtilsBean();

  private final ZeebeClientConfigurationProperties zeebeClientConfigurationProperties;
  private final CamundaClientProperties camundaClientProperties;

  public PropertyBasedZeebeWorkerValueCustomizer(
      final ZeebeClientConfigurationProperties zeebeClientConfigurationProperties,
      CamundaClientProperties camundaClientProperties) {
    this.zeebeClientConfigurationProperties = zeebeClientConfigurationProperties;
    this.camundaClientProperties = camundaClientProperties;
  }

  @Override
  public void customize(ZeebeWorkerValue zeebeWorker) {
    applyDefaultWorkerName(zeebeWorker);
    applyDefaultJobWorkerType(zeebeWorker);
    applyFetchVariables(zeebeWorker);
    applyOverrides(zeebeWorker);
  }

  private void applyFetchVariables(ZeebeWorkerValue zeebeWorkerValue) {
    if (hasActivatedJobInjected(zeebeWorkerValue)) {
      LOG.debug(
          "Worker '{}': ActivatedJob is injected, no variable filtering possible",
          zeebeWorkerValue.getName());
    } else if (zeebeWorkerValue.getForceFetchAllVariables() != null
        && zeebeWorkerValue.getForceFetchAllVariables()) {
      LOG.debug("Worker '{}': Force fetch all variables is enabled", zeebeWorkerValue.getName());
      zeebeWorkerValue.setFetchVariables(Collections.emptyList());
    } else {
      Set<String> variables = new HashSet<>();
      if (zeebeWorkerValue.getFetchVariables() != null) {
        variables.addAll(zeebeWorkerValue.getFetchVariables());
      }
      variables.addAll(
          readZeebeVariableParameters(zeebeWorkerValue.getMethodInfo()).stream()
              .map(this::extractVariableName)
              .collect(Collectors.toList()));
      variables.addAll(readVariablesAsTypeParameters(zeebeWorkerValue.getMethodInfo()));
      zeebeWorkerValue.setFetchVariables(variables.stream().collect(Collectors.toList()));
      LOG.debug(
          "Worker '{}': Fetching only required variables {}",
          zeebeWorkerValue.getName(),
          variables);
    }
  }

  private boolean hasActivatedJobInjected(ZeebeWorkerValue zeebeWorkerValue) {
    return zeebeWorkerValue.getMethodInfo().getParameters().stream()
        .anyMatch(p -> p.getParameterInfo().getType().isAssignableFrom(ActivatedJob.class));
  }

  private List<ParameterInfo> readZeebeVariableParameters(MethodInfo methodInfo) {
    List<ParameterInfo> result = methodInfo.getParametersFilteredByAnnotation(Variable.class);
    result.addAll(methodInfo.getParametersFilteredByAnnotation(ZeebeVariable.class));
    return result;
  }

  private String extractVariableName(ParameterInfo parameterInfo) {
    Variable variableAnnotation = parameterInfo.getParameterInfo().getAnnotation(Variable.class);
    if (variableAnnotation != null && !Variable.DEFAULT_NAME.equals(variableAnnotation.name())) {
      return variableAnnotation.name();
    }
    return parameterInfo.getParameterName();
  }

  private List<String> readVariablesAsTypeParameters(MethodInfo methodInfo) {
    List<String> result = new ArrayList<>();
    List<ParameterInfo> parameters =
        methodInfo.getParametersFilteredByAnnotation(VariablesAsType.class);
    parameters.addAll(methodInfo.getParametersFilteredByAnnotation(ZeebeVariablesAsType.class));
    parameters.forEach(
        pi ->
            ReflectionUtils.doWithFields(
                pi.getParameterInfo().getType(), f -> result.add(f.getName())));
    return result;
  }

  private void applyOverrides(ZeebeWorkerValue zeebeWorker) {
    final Map<String, ZeebeWorkerValue> workerConfigurationMap =
        getOrLegacyOrDefault(
            "Override",
            () -> camundaClientProperties.getZeebe().getOverride(),
            () -> zeebeClientConfigurationProperties.getWorker().getOverride(),
            new HashMap<>(),
            null);
    try {
      if (ofNullable(camundaClientProperties.getZeebe())
          .map(ZeebeClientProperties::getDefaults)
          .isPresent()) {
        BEAN_UTILS_BEAN.copyProperties(
            zeebeWorker, camundaClientProperties.getZeebe().getDefaults());
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(
          "Error while copying properties from "
              + camundaClientProperties.getZeebe().getDefaults()
              + " to "
              + zeebeWorker,
          e);
    }
    final String workerType = zeebeWorker.getType();
    if (workerConfigurationMap.containsKey(workerType)) {
      final ZeebeWorkerValue zeebeWorkerValue = workerConfigurationMap.get(workerType);
      LOG.debug("Worker '{}': Applying overrides {}", workerType, zeebeWorkerValue);
      try {
        BEAN_UTILS_BEAN.copyProperties(zeebeWorker, zeebeWorkerValue);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(
            "Error while copying properties from " + zeebeWorkerValue + " to " + zeebeWorker, e);
      }
    }
  }

  private void applyDefaultWorkerName(ZeebeWorkerValue zeebeWorker) {
    String defaultJobWorkerName =
        getOrLegacyOrDefault(
            "DefaultJobWorkerName",
            () -> camundaClientProperties.getZeebe().getDefaults().getName(),
            zeebeClientConfigurationProperties::getDefaultJobWorkerName,
            null,
            null);
    if (isBlank(zeebeWorker.getName())) {
      if (isNotBlank(defaultJobWorkerName)) {
        LOG.debug(
            "Worker '{}': Setting name to default {}", zeebeWorker.getName(), defaultJobWorkerName);
        zeebeWorker.setName(defaultJobWorkerName);
      } else {
        String generatedJobWorkerName =
            zeebeWorker.getMethodInfo().getBeanName()
                + "#"
                + zeebeWorker.getMethodInfo().getMethodName();
        LOG.debug(
            "Worker '{}': Setting name to generated {}",
            zeebeWorker.getName(),
            generatedJobWorkerName);
        zeebeWorker.setName(generatedJobWorkerName);
      }
    }
  }

  private void applyDefaultJobWorkerType(ZeebeWorkerValue zeebeWorker) {
    String defaultJobWorkerType =
        getOrLegacyOrDefault(
            "DefaultJobWorkerType",
            () -> camundaClientProperties.getZeebe().getDefaults().getType(),
            zeebeClientConfigurationProperties::getDefaultJobWorkerType,
            null,
            null);
    if (isBlank(zeebeWorker.getType())) {
      if (isNotBlank(defaultJobWorkerType)) {
        LOG.debug(
            "Worker '{}': Setting type to default {}", zeebeWorker.getName(), defaultJobWorkerType);
        zeebeWorker.setType(defaultJobWorkerType);
      } else {
        String generatedJobWorkerType = zeebeWorker.getMethodInfo().getMethodName();
        LOG.debug(
            "Worker '{}': Setting type to generated {}",
            zeebeWorker.getName(),
            generatedJobWorkerType);
        zeebeWorker.setType(generatedJobWorkerType);
      }
    }
  }
}
