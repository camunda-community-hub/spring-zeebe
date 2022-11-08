/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.camunda.zeebe.spring.client.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.error.BpmnError;
import io.camunda.zeebe.spring.client.feel.FeelEngineWrapper;
import io.camunda.zeebe.spring.client.feel.FeelEngineWrapperException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The ConnectorHelper provide utility functions used to build connector runtimes. */
public class ConnectorHelper {

  public static FeelEngineWrapper FEEL_ENGINE_WRAPPER = new FeelEngineWrapper();
  // TODO: Check if this is properly configured
  public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String ERROR_CANNOT_PARSE_VARIABLES = "Cannot parse variables";

  public static final String RESULT_VARIABLE_HEADER_NAME = "resultVariable";
  public static final String RESULT_EXPRESSION_HEADER_NAME = "resultExpression";
  public static final String ERROR_EXPRESSION_HEADER_NAME = "errorExpression";

  /**
   * @return a map with output process variables for a given response from an {@link
   *     io.camunda.connector.api.outbound.OutboundConnectorFunction} conigured with headers from a
   *     Zeebe Job.
   */
  public static Map<String, Object> createOutputVariables(
      final Object responseContent, final Map<String, String> jobHeaders) {
    final Map<String, Object> outputVariables = new HashMap<>();
    final String resultVariableName = jobHeaders.get(RESULT_VARIABLE_HEADER_NAME);
    final String resultExpression = jobHeaders.get(RESULT_EXPRESSION_HEADER_NAME);

    if (resultVariableName != null && resultVariableName.trim().length()>0) {
      outputVariables.put(resultVariableName, responseContent);
    }

    Optional.ofNullable(resultExpression)
        .filter(s -> s!=null && s.trim().length()>0)
        .map(expression -> FEEL_ENGINE_WRAPPER.evaluateToJson(expression, responseContent))
        .map(json -> parseJsonVarsAsTypeOrThrow(json, Map.class, resultExpression))
        .ifPresent(outputVariables::putAll);

    return outputVariables;
  }

  public static Optional<BpmnError> examineErrorExpression(
      final Object responseContent, final Map<String, String> jobHeaders) {
    final String errorExpression = jobHeaders.get(ERROR_EXPRESSION_HEADER_NAME);
    return Optional.ofNullable(errorExpression)
        .filter(s -> s!=null && s.trim().length()>0)
        .map(expression -> FEEL_ENGINE_WRAPPER.evaluateToJson(expression, responseContent))
        .map(json -> parseJsonVarsAsTypeOrThrow(json, BpmnError.class, errorExpression))
        .filter(BpmnError::hasCode);
  }

  private static <T> T parseJsonVarsAsTypeOrThrow(
      final String jsonVars, Class<T> type, final String expression) {
    try {
      return OBJECT_MAPPER.readValue(jsonVars, type);
    } catch (JsonProcessingException e) {
      throw new FeelEngineWrapperException(ERROR_CANNOT_PARSE_VARIABLES, expression, jsonVars, e);
    }
  }
}
