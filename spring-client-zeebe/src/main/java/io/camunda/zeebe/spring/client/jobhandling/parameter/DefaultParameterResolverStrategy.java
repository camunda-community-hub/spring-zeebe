package io.camunda.zeebe.spring.client.jobhandling.parameter;

import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.CustomHeaders;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import io.camunda.zeebe.spring.client.annotation.ZeebeCustomHeaders;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariablesAsType;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultParameterResolverStrategy implements ParameterResolverStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultParameterResolverStrategy.class);
  protected final JsonMapper jsonMapper;

  public DefaultParameterResolverStrategy(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  public ParameterResolver createResolver(ParameterInfo parameterInfo) {
    Class<?> parameterType = parameterInfo.getParameterInfo().getType();
    if (JobClient.class.isAssignableFrom(parameterType)) {
      return new JobClientParameterResolver();
    } else if (ActivatedJob.class.isAssignableFrom(parameterType)) {
      return new ActivatedJobParameterResolver();
    } else if (isVariable(parameterInfo)) {
      String variableName = getVariableName(parameterInfo);
      return new VariableResolver(variableName, parameterType, jsonMapper);
    } else if (isVariablesAsType(parameterInfo)) {
      return new VariablesAsTypeResolver(parameterType);
    } else if (isCustomHeaders(parameterInfo)) {
      return new CustomHeadersResolver();
    }
    throw new IllegalStateException(
        "Could not create parameter resolver for parameter " + parameterInfo);
  }

  protected boolean isVariable(ParameterInfo parameterInfo) {
    return parameterInfo.getParameterInfo().isAnnotationPresent(Variable.class)
        || parameterInfo.getParameterInfo().isAnnotationPresent(ZeebeVariable.class);
  }

  protected boolean isVariablesAsType(ParameterInfo parameterInfo) {
    return parameterInfo.getParameterInfo().isAnnotationPresent(VariablesAsType.class)
        || parameterInfo.getParameterInfo().isAnnotationPresent(ZeebeVariablesAsType.class);
  }

  protected boolean isCustomHeaders(ParameterInfo parameterInfo) {
    return parameterInfo.getParameterInfo().isAnnotationPresent(CustomHeaders.class)
        || parameterInfo.getParameterInfo().isAnnotationPresent(ZeebeCustomHeaders.class);
  }

  protected String getVariableName(ParameterInfo param) {
    if (param.getParameterInfo().isAnnotationPresent(Variable.class)) {
      String nameFromAnnotation = param.getParameterInfo().getAnnotation(Variable.class).name();
      if (!Objects.equals(nameFromAnnotation, Variable.DEFAULT_NAME)) {
        LOG.trace("Extracting name {} from Variable.name", nameFromAnnotation);
        return nameFromAnnotation;
      }
      String valueFromAnnotation = param.getParameterInfo().getAnnotation(Variable.class).value();
      if (!Objects.equals(valueFromAnnotation, Variable.DEFAULT_NAME)) {
        LOG.trace("Extracting name {} from Variable.value", valueFromAnnotation);
        return valueFromAnnotation;
      }
    }
    LOG.trace("Extracting variable name from parameter name");
    return param.getParameterName();
  }
}
