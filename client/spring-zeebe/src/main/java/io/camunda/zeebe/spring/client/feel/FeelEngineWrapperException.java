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
package io.camunda.zeebe.spring.client.feel;

/** Exception class indicating issues in a {@link FeelEngineWrapper} call */
public class FeelEngineWrapperException extends RuntimeException {

  public FeelEngineWrapperException(
      final String reason, final String expression, final Object context) {
    this(reason, expression, context, null);
  }

  public FeelEngineWrapperException(
      final String reason,
      final String expression,
      final Object context,
      final Throwable throwable) {
    super(
        String.format(
            "Failed to evaluate expression '%s' in context '%s'. Reason: %s",
            expression, context, reason),
        throwable);
  }
}
