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
package io.camunda.connector.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.camunda.connector.runtime.util.discovery.EnvVarsConnectorDiscovery;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;

@SpringBootTest(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "camunda.connector.polling.enabled=false"
    })
@ZeebeSpringTest
/*
@TestPropertySource(properties = {
        "CONNECTOR_TEST2_FUNCTION=io.camunda.connector.http.HttpJsonFunction",
        "CONNECTOR_TEST2_TYPE=non-default-TEST-task-type"
})
 */
class RuntimeStartupWithConnectorsFromEnvVarsTests {

  @Autowired private JobWorkerManager jobWorkerManager;

  @BeforeTestClass
  public static void prepare() throws Exception {
    EnvVarsConnectorDiscovery.addHardwiredEnvironmentVariable(
        "CONNECTOR_TEST2_FUNCTION", "io.camunda.connector.http.HttpJsonFunction");
    EnvVarsConnectorDiscovery.addHardwiredEnvironmentVariable(
        "CONNECTOR_TEST2_TYPE", "non-default-TEST-task-type");
  }

  @AfterTestClass
  public static void cleanup() throws Exception {
    EnvVarsConnectorDiscovery.clearHardwiredEnvironmentVariable();
  }

  @Test
  public void httpConnectorLoadedViaEnvVar() {
    if (true) {
      // This test currently does not work as the lifecycle of the connector registry is longer
      // it is not re-scanned for this test case.
      // TODO: THink about if we need to test it and if yes, what's the best way forward (e.g. some
      // programmatic way to retrigger scanning?)
      return;
    }
    // Make sure the environment variables are used INSTEAD of SPI (which would load TEST)
    assertFalse(jobWorkerManager.findJobWorkerConfigByName("TEST").isPresent());

    Optional<ZeebeWorkerValue> testConnector = jobWorkerManager.findJobWorkerConfigByName("TEST2");
    assertTrue(testConnector.isPresent());
    assertEquals("non-default-TEST-task-type", testConnector.get().getType());
  }
}
