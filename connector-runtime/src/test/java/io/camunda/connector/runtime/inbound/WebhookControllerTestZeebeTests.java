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
package io.camunda.connector.runtime.inbound;

import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.runtime.ConnectorRuntimeApplication;
import io.camunda.connector.runtime.inbound.event.StartEventInboundTarget;
import io.camunda.connector.runtime.inbound.registry.WebhookConnectorRegistry;
import io.camunda.connector.runtime.inbound.webhook.InboundWebhookRestController;
import io.camunda.connector.runtime.inbound.webhook.WebhookResponse;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static io.camunda.connector.runtime.inbound.WebhookControllerPlainJavaTests.webhookProperties;
import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = ConnectorRuntimeApplication.class,
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "camunda.connector.webhook.enabled=true"
    })
@ZeebeSpringTest
@ExtendWith(MockitoExtension.class)
class WebhookControllerTestZeebeTests {

  @Test
  public void contextLoaded() {}

  @Autowired private WebhookConnectorRegistry registry;

  @Autowired private ZeebeClient zeebeClient;

  @Autowired @InjectMocks private InboundWebhookRestController controller;

  // This test is wired by Spring - but this is not really giving us any advantage
  // Better move to plain Java as shown in InboundWebhookRestControllerTests
  @Test
  public void multipleWebhooksOnSameContextPath() throws IOException {
    deployProcess("processA");
    deployProcess("processB");

    registry.reset();
    registry.registerWebhookConnector(webhookProperties("processA", "myPath", zeebeClient));
    registry.registerWebhookConnector(webhookProperties("processB", "myPath", zeebeClient));
    ;

    ResponseEntity<WebhookResponse> responseEntity =
        controller.inbound("myPath", "{}".getBytes(), new HashMap<>());

    assertEquals(200, responseEntity.getStatusCode().value());
    assertTrue(responseEntity.getBody().getUnauthorizedConnectors().isEmpty());
    assertTrue(responseEntity.getBody().getUnactivatedConnectors().isEmpty());
    assertEquals(2, responseEntity.getBody().getExecutedConnectors().size());
    assertEquals(
        Set.of("webhook-myPath-processA-1", "webhook-myPath-processB-1"),
        responseEntity.getBody().getExecutedConnectors().keySet());

    InboundConnectorResult piA =
        responseEntity.getBody().getExecutedConnectors().get("webhook-myPath-processA-1");
    assertInstanceOf(StartEventInboundTarget.Response.class, piA);
    ProcessInstanceEvent piEventA = ((StartEventInboundTarget.Response) piA).getProcessInstanceEvent();

    waitForProcessInstanceCompleted(piEventA);
    assertThat(piEventA).isCompleted();

    InboundConnectorResult piB =
        responseEntity.getBody().getExecutedConnectors().get("webhook-myPath-processB-1");
    assertInstanceOf(StartEventInboundTarget.Response.class, piB);
    ProcessInstanceEvent piEventB = ((StartEventInboundTarget.Response) piB).getProcessInstanceEvent();

    waitForProcessInstanceCompleted(piEventB);
    assertThat(piEventB).isCompleted();
  }

  public void deployProcess(String bpmnProcessId) {
    zeebeClient
        .newDeployResourceCommand()
        .addProcessModel(
            Bpmn.createExecutableProcess(bpmnProcessId).startEvent().endEvent().done(),
            bpmnProcessId + ".bpmn")
        .send()
        .join();
  }
}
