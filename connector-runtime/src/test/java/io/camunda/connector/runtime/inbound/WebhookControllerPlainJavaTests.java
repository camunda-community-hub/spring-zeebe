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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import io.camunda.connector.impl.inbound.correlation.StartEventCorrelationPoint;
import io.camunda.connector.runtime.inbound.util.InboundConnectorContextBuilder;
import io.camunda.connector.runtime.inbound.util.command.CreateCommandDummy;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnector;
import io.camunda.zeebe.spring.client.metrics.SimpleMetricsRecorder;
import io.camunda.connector.runtime.inbound.webhook.InboundWebhookRestController;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorProperties;
import io.camunda.connector.runtime.inbound.webhook.WebhookResponse;
import io.camunda.connector.runtime.util.feel.FeelEngineWrapper;
import io.camunda.zeebe.client.ZeebeClient;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class WebhookControllerPlainJavaTests {

  private SimpleMetricsRecorder metrics;

  ZeebeClient zeebeClient;
  InboundConnectorContext connectorContext;

  @BeforeEach
  public void setupMetrics() {
    metrics = new SimpleMetricsRecorder();
    zeebeClient = mock(ZeebeClient.class);
    connectorContext = InboundConnectorContextBuilder.create()
      .secret("DUMMY_SECRET", "s3cr3T")
      .zeebeClient(zeebeClient)
      .build();
  }

  @Test
  public void multipleWebhooksOnSameContextPath() throws IOException {
    WebhookConnector webhook = new WebhookConnector();


    when(zeebeClient.newCreateInstanceCommand()).thenReturn(new CreateCommandDummy());
    InboundWebhookRestController controller =
        new InboundWebhookRestController(
            connectorContext, new FeelEngineWrapper(), webhook, new ObjectMapper(), metrics);

    webhook.activate(webhookProperties("processA", 1, "myPath"), connectorContext);
    webhook.activate(webhookProperties("processB", 1, "myPath"), connectorContext);

    ResponseEntity<WebhookResponse> responseEntity =
        controller.inbound("myPath", "{}".getBytes(), new HashMap<>());

    assertEquals(200, responseEntity.getStatusCode().value());
    assertTrue(responseEntity.getBody().getUnauthorizedConnectors().isEmpty());
    assertTrue(responseEntity.getBody().getUnactivatedConnectors().isEmpty());
    assertEquals(2, responseEntity.getBody().getExecutedConnectors().size());
    assertEquals(
        Set.of("webhook-myPath-processA-1", "webhook-myPath-processB-1"),
        responseEntity.getBody().getExecutedConnectors().keySet());
    assertEquals(1, metrics.getCount(MetricsRecorder.METRIC_NAME_INBOUND_CONNECTOR, MetricsRecorder.ACTION_ACTIVATED, WebhookConnector.TYPE_WEBHOOK));
    assertEquals(1, metrics.getCount(MetricsRecorder.METRIC_NAME_INBOUND_CONNECTOR, MetricsRecorder.ACTION_COMPLETED, WebhookConnector.TYPE_WEBHOOK));
    assertEquals(0, metrics.getCount(MetricsRecorder.METRIC_NAME_INBOUND_CONNECTOR, MetricsRecorder.ACTION_FAILED, WebhookConnector.TYPE_WEBHOOK));
  }


  @Test
  public void webhookMultipleVersionsDisableWebhook() throws IOException {
    WebhookConnector webhook = new WebhookConnector();

    var processA1 = webhookProperties("processA", 1, "myPath");
    var processA2 = webhookProperties("processA", 2, "myPath");
    var processB1 = webhookProperties("processB", 1, "myPath2");

    webhook.activate(processA1, connectorContext);
    webhook.deactivate(processA1);
    webhook.activate(processA2, connectorContext);

    webhook.activate(processB1, connectorContext);
    webhook.deactivate(processB1);

    Collection<WebhookConnectorProperties> connectors1 =
        webhook.getWebhookConnectorByContextPath("myPath");

    assertEquals(1, connectors1.size()); // only one
    assertEquals(2, connectors1.iterator().next().getProcessDefinitionVersion()); // And the newest one

    Collection<WebhookConnectorProperties> connectors2 =
        webhook.getWebhookConnectorByContextPath("myPath2");
    assertEquals(0, connectors2.size()); // No one - as it was disabled
  }

  private static long nextProcessDefinitionKey = 0L;

  public static InboundConnectorProperties webhookProperties(
      String bpmnProcessId, int version, String contextPath) {
    return webhookProperties(++nextProcessDefinitionKey, bpmnProcessId, version, contextPath);
  }

  public static InboundConnectorProperties webhookProperties(
    long processDefinitionKey, String bpmnProcessId, int version, String contextPath) {

    return new InboundConnectorProperties(
      new StartEventCorrelationPoint(processDefinitionKey, bpmnProcessId, version),
      Map.of(
        "inbound.type", "webhook",
        "inbound.context", contextPath,
        "inbound.secretExtractor", "=\"TEST\"",
        "inbound.secret", "TEST",
        "inbound.activationCondition", "=true",
        "inbound.variableMapping", "={}"),
      bpmnProcessId,
      version,
      processDefinitionKey);
  }
}
