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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorRegistry;
import io.camunda.connector.runtime.inbound.webhook.InboundWebhookRestController;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorProperties;
import io.camunda.connector.runtime.inbound.webhook.WebhookResponse;
import io.camunda.connector.test.inbound.InboundConnectorContextBuilder;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.impl.ZeebeClientFutureImpl;
import io.camunda.zeebe.spring.client.feel.FeelEngineWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebhookControllerPlainJavaTests {

  @Test
  public void multipleWebhooksOnSameContextPath() throws IOException {
    InboundConnectorRegistry registry = new InboundConnectorRegistry();
    InboundConnectorContext connectorContext =
        InboundConnectorContextBuilder.create().secret("DUMMY_SECRET", "s3cr3T").build();
    ZeebeClient zeebeClient = mock(ZeebeClient.class);
    when(zeebeClient.newCreateInstanceCommand()).thenReturn(new CreateCommandDummy());
    InboundWebhookRestController controller =
        new InboundWebhookRestController(
            registry, connectorContext, zeebeClient, new FeelEngineWrapper(), new ObjectMapper());

    registry.reset();
    // registry.markProcessDefinitionChecked(123, "processA", 1);
    registry.registerWebhookConnector(webhookProperties("processA", "myPath"));
    // registry.markProcessDefinitionChecked(124, "processB", 1);
    registry.registerWebhookConnector(webhookProperties("processB", "myPath"));
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
  }

  @Test
  public void webhookMultipleVersions() throws IOException {
    // see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
    InboundConnectorRegistry registry = new InboundConnectorRegistry();

    register(registry, "processA", 1, "myPath");
    register(registry, "processA", 2, "myPath");
    register(registry, "processA", 3, "myPath2");
    register(registry, "processA", 4, "myPath2");

    Collection<WebhookConnectorProperties> connectors1 =
        registry.getWebhookConnectorByContextPath("myPath");
    assertEquals(1, connectors1.size()); // only one
    assertEquals(2, connectors1.iterator().next().getVersion()); // And the newest one

    Collection<WebhookConnectorProperties> connectors2 =
        registry.getWebhookConnectorByContextPath("myPath2");
    assertEquals(1, connectors2.size()); // only one
    assertEquals(4, connectors2.iterator().next().getVersion()); // And the newest one
  }

  @Test
  public void webhookMultipleVersionsDisableWebhook() throws IOException {
    // see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
    InboundConnectorRegistry registry = new InboundConnectorRegistry();

    register(registry, "processA", 1, "myPath");
    register(registry, "processA", 2, "myPath");
    register(registry, "processA", 3, "myPath2");
    register(registry, "processA", 4, null); // disabling Webhook on "myPath2"

    Collection<WebhookConnectorProperties> connectors1 =
        registry.getWebhookConnectorByContextPath("myPath");
    assertEquals(1, connectors1.size()); // only one
    assertEquals(2, connectors1.iterator().next().getVersion()); // And the newest one

    Collection<WebhookConnectorProperties> connectors2 =
        registry.getWebhookConnectorByContextPath("myPath2");
    assertNull(connectors2); // No one - as it was disabled
  }

  @Test
  public void webhookMultipleVersionsDisableWebhook2() throws IOException {
    // see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
    InboundConnectorRegistry registry = new InboundConnectorRegistry();

    register(registry, "processA", 1, "myPath");
    register(registry, "processA", 2, "myPath");
    register(registry, "processA", 3, null); // disabling Webhook on "myPath2"

    Collection<WebhookConnectorProperties> connectors1 =
        registry.getWebhookConnectorByContextPath("myPath");
    assertNull(connectors1); // No one - as it was disabled
  }

  @Test
  public void webhookMultipleVersionsReenablingWebhook2() throws IOException {
    // see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
    InboundConnectorRegistry registry = new InboundConnectorRegistry();

    register(registry, "processA", 1, "myPath");
    register(registry, "processA", 2, null);
    register(registry, "processA", 3, "myPath"); // disabling Webhook on "myPath2"

    Collection<WebhookConnectorProperties> connectors1 =
        registry.getWebhookConnectorByContextPath("myPath");
    assertEquals(1, connectors1.size()); // only one
    assertEquals(3, connectors1.iterator().next().getVersion()); // And the newest one
  }

  @Test
  public void webhookMultipleProcessDefinitionsAndVersions() throws IOException {
    // see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
    InboundConnectorRegistry registry = new InboundConnectorRegistry();

    register(registry, 1, "processA", 1, "myPath");
    register(registry, 2, "processA", 2, "myPath");
    register(registry, 3, "processA", 3, "myPath2");
    register(registry, 4, "processB", 1, "myPath");
    register(registry, 5, "processB", 2, "myPath");

    Collection<WebhookConnectorProperties> connectors1 =
        registry.getWebhookConnectorByContextPath("myPath");
    assertThat(connectors1)
        .hasSize(2)
        .extracting(WebhookConnectorProperties::getProcessDefinitionKey)
        .containsExactly(2l, 5l);
  }

  @Test
  public void webhookMultipleProcessDefinitionsAndVersionsAndDisabledWebhook() throws IOException {
    // see https://github.com/camunda/connector-sdk-inbound-webhook/issues/24#issue-1416083859
    InboundConnectorRegistry registry = new InboundConnectorRegistry();

    register(registry, 1, "processA", 1, "myPath");
    register(registry, 2, "processA", 2, "myPath");
    register(registry, 3, "processA", 3, null); // disabling Webhook on "myPath"
    register(registry, 4, "processB", 1, "myPath");
    register(registry, 5, "processB", 2, "myPath");

    Collection<WebhookConnectorProperties> connectors1 =
        registry.getWebhookConnectorByContextPath("myPath");
    assertThat(connectors1)
        .hasSize(1)
        .extracting(WebhookConnectorProperties::getProcessDefinitionKey)
        .containsExactly(5l);
  }

  private static long nextProcessDefinitionKey = 1;

  public static void register(
      InboundConnectorRegistry registry, String bpmnProcessId, int version, String contextPath) {
    register(registry, ++nextProcessDefinitionKey, bpmnProcessId, version, contextPath);
  }

  public static void register(
      InboundConnectorRegistry registry,
      long processDefinitionKey,
      String bpmnProcessId,
      int version,
      String contextPath) {
    registry.markProcessDefinitionChecked(processDefinitionKey, bpmnProcessId, version);
    if (contextPath != null) {
      registry.registerWebhookConnector(
          webhookProperties(processDefinitionKey, bpmnProcessId, version, contextPath));
    }
  }

  public static InboundConnectorProperties webhookProperties(
      String bpmnProcessId, String contextPath) {
    return webhookProperties(123l, bpmnProcessId, 1, contextPath);
  }

  public static InboundConnectorProperties webhookProperties(
      long processDefinitionKey, String bpmnProcessId, int version, String contextPath) {

    HashMap<String, String> properties = new HashMap<>();
    properties.put("inbound.type", "webhook");
    properties.put("inbound.context", contextPath);
    properties.put("inbound.secretExtractor", "=\"TEST\"");
    properties.put("inbound.secret", "TEST");
    properties.put("inbound.activationCondition", "=true");
    properties.put("inbound.variableMapping", "={}");

    return new InboundConnectorProperties(
        bpmnProcessId,
        version,
        processDefinitionKey,
        properties);
  }

  public static class ProcessInstanceEventDummy implements ProcessInstanceEvent {
    public long getProcessDefinitionKey() {
      return 0;
    }

    public String getBpmnProcessId() {
      return null;
    }

    public int getVersion() {
      return 0;
    }

    public long getProcessInstanceKey() {
      return 0;
    }
  }

  public static class CreateCommandDummy
      implements CreateProcessInstanceCommandStep1,
          CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2,
          CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3 {
    public CreateProcessInstanceCommandStep2 bpmnProcessId(String bpmnProcessId) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 processDefinitionKey(long processDefinitionKey) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 version(int version) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 latestVersion() {
      return this;
    }

    public CreateProcessInstanceCommandStep3 variables(InputStream variables) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 variables(String variables) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 variables(Map<String, Object> variables) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 variables(Object variables) {
      return this;
    }

    public CreateProcessInstanceCommandStep3 startBeforeElement(String elementId) {
      return this;
    }

    public CreateProcessInstanceWithResultCommandStep1 withResult() {
      return null;
    }

    public FinalCommandStep<ProcessInstanceEvent> requestTimeout(Duration requestTimeout) {
      return null;
    }

    public ZeebeFuture<ProcessInstanceEvent> send() {
      ZeebeClientFutureImpl future = new ZeebeClientFutureImpl<>();
      future.complete(new ProcessInstanceEventDummy());
      return future;
    }
  }
}
