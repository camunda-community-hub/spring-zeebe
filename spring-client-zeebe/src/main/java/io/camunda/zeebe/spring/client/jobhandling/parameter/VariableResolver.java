package io.camunda.zeebe.spring.client.jobhandling.parameter;

import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

public class VariableResolver implements ParameterResolver {
  private final String variableName;
  private final Class<?> variableType;
  private final JsonMapper jsonMapper;

  public VariableResolver(String variableName, Class<?> variableType, JsonMapper jsonMapper) {
    this.variableName = variableName;
    this.variableType = variableType;
    this.jsonMapper = jsonMapper;
  }

  @Override
  public Object resolve(JobClient jobClient, ActivatedJob job) {
    Object variableValue = job.getVariablesAsMap().get(variableName);
    try {
      return mapZeebeVariable(variableValue, variableType);
    } catch (ClassCastException | IllegalArgumentException ex) {
      throw new RuntimeException(
          "Cannot assign process variable '"
              + variableName
              + "' to parameter when executing job '"
              + job.getType()
              + "', invalid type found: "
              + ex.getMessage());
    }
  }

  private <T> T mapZeebeVariable(Object toMap, Class<T> clazz) {
    if (toMap != null && !clazz.isInstance(toMap)) {
      //      if (jsonMapper != null) {
      return jsonMapper.fromJson(jsonMapper.toJson(toMap), clazz);
      //      }
      //      return DEFAULT_OBJECT_MAPPER.convertValue(toMap, clazz);
    } else {
      return clazz.cast(toMap);
    }
  }
}
