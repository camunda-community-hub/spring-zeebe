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
package io.camunda.zeebe.spring.client.jobhandling.copy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.scala.DefaultScalaModule$;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.feel.FeelEngine;
import org.camunda.feel.impl.SpiServiceLoader;
import scala.jdk.javaapi.CollectionConverters;
import scala.util.Either;

public class FeelEngineWrapper {

  static final String RESPONSE_MAP_KEY = "response";
  static final String ERROR_VARIABLES_MUST_NOT_BE_NULL = "variables cannot be null";
  static final String ERROR_EXPRESSION_EVALUATION_FAILED = "expression evaluation failed";

  static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {};

  private final FeelEngine feelEngine;
  private final ObjectMapper objectMapper;

  public FeelEngineWrapper() {
    this(
        new FeelEngine.Builder()
            .valueMapper(SpiServiceLoader.loadValueMapper())
            .functionProvider(SpiServiceLoader.loadFunctionProvider())
            .build(),
        new ObjectMapper()
            .registerModule(DefaultScalaModule$.MODULE$)
            // deserialize unknown types as empty objects
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS));
  }

  public FeelEngineWrapper(final FeelEngine feelEngine, final ObjectMapper objectMapper) {
    this.feelEngine = feelEngine;
    this.objectMapper = objectMapper;
  }

  private static String trimExpression(final String expression) {
    String feelExpression = expression.trim();
    if (feelExpression.startsWith("=")) {
      feelExpression = feelExpression.substring(1);
    }
    return feelExpression.trim();
  }

  private static scala.collection.immutable.Map<String, Object> toScalaMap(
      final Map<String, Object> responseMap) {
    final HashMap<String, Object> context = new HashMap<>(responseMap);
    context.put(RESPONSE_MAP_KEY, responseMap);
    return scala.collection.immutable.Map.from(CollectionConverters.asScala(context));
  }

  private Map<String, Object> ensureVariablesMap(final Object variables) {
    Objects.requireNonNull(variables, ERROR_VARIABLES_MUST_NOT_BE_NULL);
    return objectMapper.convertValue(variables, MAP_TYPE_REFERENCE);
  }

  public String evaluateToJson(final String expression, final Object variables) {
    try {
      Map<String, Object> variablesAsMap = ensureVariablesMap(variables);
      scala.collection.immutable.Map<String, Object> variablesAsMapAsScalaMap = toScalaMap(variablesAsMap);
      Either<FeelEngine.Failure, Object> result = feelEngine.evalExpression(trimExpression(expression), variablesAsMapAsScalaMap);
      if (result.isRight()) {
        return resultToJson(result.right().get());
      } else {
        throw new RuntimeException(result.left().get().message());
      }
    } catch (Exception e) {
      throw new FeelEngineWrapperException(
          ERROR_EXPRESSION_EVALUATION_FAILED, expression, variables, e);
    }
  }

  private String resultToJson(final Object result) {
    try {
      return objectMapper.writeValueAsString(result);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
