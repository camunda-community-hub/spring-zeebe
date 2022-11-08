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
package io.camunda.zeebe.spring.client.connector.outbound;

import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.camunda.zeebe.spring.client.connector.OutboundConnectorRegistrationHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

public class OutboundConnectorRegistrationTest {

  private static <T> T withEnvVars(Object[] vars, Callable<T> fn) throws Exception {
    return new EnvironmentVariables().set(vars).execute(fn);
  }

  @Test
  public void shouldConfigureThroughEnv() throws Exception {

    // given
    Object[] env =
        new Object[] {
          // shall be picked up with meta-data + overrides
          "CONNECTOR_ANNOTATED_OVERRIDE_FUNCTION",
          "io.camunda.zeebe.spring.client.connector.outbound.AnnotatedFunction",
          "CONNECTOR_ANNOTATED_OVERRIDE_TYPE",
          "io.camunda:annotated-override",

          // shall be picked up with meta-data
          "CONNECTOR_ANNOTATED_FUNCTION",
          "io.camunda.zeebe.spring.client.connector.outbound.AnnotatedFunction",

          // shall be picked up despite no meta-data
          "CONNECTOR_NOT_ANNOTATED_FUNCTION",
          "io.camunda.zeebe.spring.client.connector.outbound.NotAnnotatedFunction",
          "CONNECTOR_NOT_ANNOTATED_TYPE",
          "io.camunda:not-annotated",
          "CONNECTOR_NOT_ANNOTATED_INPUT_VARIABLES",
          "foo,bar"
        };

    // when
    List<OutboundConnectorConfiguration> registrations =
        withEnvVars(
            env,
            () -> {
              return OutboundConnectorRegistrationHelper.parse();
            });

    // then
    Assertions.assertThat(registrations).hasSize(3);

    assertRegistration(
        registrations,
        "ANNOTATED_OVERRIDE",
        "io.camunda:annotated-override",
        new String[] {"a", "b"},
        AnnotatedFunction.class.getName());

    assertRegistration(
        registrations,
        "ANNOTATED",
        "io.camunda:annotated",
        new String[] {"a", "b"},
        AnnotatedFunction.class.getName());

    assertRegistration(
        registrations,
        "NOT_ANNOTATED",
        "io.camunda:not-annotated",
        new String[] {"foo", "bar"},
        NotAnnotatedFunction.class.getName());
  }

  @Test
  public void shouldConfigureThroughEnv_FailOnIncompleteConfiguration() {

    // given
    Object[] env =
        new Object[] {
          "CONNECTOR_NOT_ANNOTATED_FUNCTION",
          "io.camunda.zeebe.spring.client.connector.outbound.NotAnnotatedFunction",
          "CONNECTOR_NOT_ANNOTATED_INPUT_VARIABLES",
          "foo,bar"
        };

    // then
    Assertions.assertThatThrownBy(
            () -> withEnvVars(env, () -> OutboundConnectorRegistrationHelper.parse()))
        .hasMessage(
            "Type not specified: Please configure it via CONNECTOR_NOT_ANNOTATED_TYPE environment variable");
  }

  @Test
  public void shouldConfigureThroughEnv_FailOnClassNotFound() {

    // given
    Object[] env =
        new Object[] {
          "CONNECTOR_NOT_FOUND_FUNCTION",
          "io.camunda.connector.runtime.jobworker.impl.outbound.NotFound"
        };

    // then
    Assertions.assertThatThrownBy(
            () -> withEnvVars(env, () -> OutboundConnectorRegistrationHelper.parse()))
        .hasMessage("Failed to load io.camunda.connector.runtime.jobworker.impl.outbound.NotFound");
  }

  @Test
  public void shouldConfigureViaSPI() {

    // when
    List<OutboundConnectorConfiguration> registrations =
        OutboundConnectorRegistrationHelper.parse();

    // then
    Assertions.assertThat(registrations).hasSize(1);

    assertRegistration(
        registrations,
        "ANNOTATED",
        "io.camunda:annotated",
        new String[] {"a", "b"},
        AnnotatedFunction.class.getName());
  }

  private static void assertRegistration(
      List<OutboundConnectorConfiguration> registrations,
      String name,
      String type,
      String[] inputVariables,
      String functionCls) {

    Assertions.assertThatList(registrations)
        .anyMatch(
            registration -> {
              return (registration.getName().equals(name)
                  && registration.getType().equals(type)
                  && Arrays.equals(registration.getInputVariables(), inputVariables)
                  && registration.getFunction().getClass().getName().equals(functionCls));
            });
  }
}
