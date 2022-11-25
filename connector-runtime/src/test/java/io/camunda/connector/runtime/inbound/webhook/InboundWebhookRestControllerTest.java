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
package io.camunda.connector.runtime.inbound.webhook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorRegistry;
import io.camunda.connector.runtime.inbound.signature.HMACSwitchCustomerChoice;
import io.camunda.connector.runtime.inbound.webhook.InboundWebhookRestController;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorProperties;
import io.camunda.connector.runtime.inbound.webhook.WebhookResponse;
import io.camunda.connector.runtime.util.feel.FeelEngineWrapper;
import io.camunda.zeebe.client.ZeebeClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import io.camunda.zeebe.spring.client.connector.MetricsRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class InboundWebhookRestControllerTest {

  private static final String DEFAULT_CONTEXT = "contextPath";
  private static final byte[] DEFAULT_REQUEST_BODY =
      "{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8);
  private static final Map<String, String> DEFAULT_HEADERS = Map.of("x-signature", "sha1=aabbccdd");

  @Mock private InboundConnectorRegistry registry;
  @Mock private InboundConnectorContext connectorContext;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ZeebeClient zeebeClient;

  @Mock private FeelEngineWrapper feelEngine;
  @Mock private MetricsRecorder metrics;
  @Spy private ObjectMapper jsonMapper;

  @InjectMocks private InboundWebhookRestController controller;

  @Test
  void inbound_HappyCase_ReturnsExecutedConnectorMessage() throws IOException {
    final String evaluationExpression = "=a=b";
    final String variablesMapping = "={x: response.key}";
    InboundConnectorProperties connectorProps =
        new InboundConnectorProperties(
            "proc-id",
            1,
            2,
            Map.of(
                "inbound.context", DEFAULT_CONTEXT,
                "inbound.activationCondition", evaluationExpression,
                "inbound.variableMapping", variablesMapping,
                "inbound.shouldValidateHmac", HMACSwitchCustomerChoice.disabled.name()));
    WebhookConnectorProperties props = new WebhookConnectorProperties(connectorProps);
    when(registry.containsContextPath(DEFAULT_CONTEXT)).thenReturn(true);
    when(registry.getWebhookConnectorByContextPath(DEFAULT_CONTEXT)).thenReturn(List.of(props));
    when(feelEngine.evaluate(eq(evaluationExpression), any(Map.class))).thenReturn(true);
    when(feelEngine.evaluate(eq(variablesMapping), any(Map.class))).thenReturn(Map.of());

    ResponseEntity<WebhookResponse> response =
        controller.inbound(DEFAULT_CONTEXT, DEFAULT_REQUEST_BODY, DEFAULT_HEADERS);

    verify(connectorContext).replaceSecrets(props);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getUnauthorizedConnectors()).isEmpty();
    assertThat(response.getBody().getExecutedConnectors()).isNotEmpty();
    assertThat(response.getBody().getExecutedConnectors())
        .containsKey(props.getConnectorIdentifier());
    assertThat(response.getBody().getUnactivatedConnectors()).isEmpty();
  }

  @Test
  void inbound_RegistryHasNoContextPath_ThrowsNotFoundException() {
    when(registry.containsContextPath(DEFAULT_CONTEXT)).thenReturn(false);
    assertThrows(
        ResponseStatusException.class,
        () -> controller.inbound(DEFAULT_CONTEXT, DEFAULT_REQUEST_BODY, DEFAULT_HEADERS));
  }

  @Test
  void inbound_HmacValidationFailed_ReturnsUnauthorizedConnectorMessage() throws IOException {
    InboundConnectorProperties connectorProps =
        new InboundConnectorProperties(
            "proc-id",
            1,
            2,
            Map.of(
                "inbound.context", DEFAULT_CONTEXT,
                "inbound.activationCondition", "",
                "inbound.variableMapping", "",
                "inbound.shouldValidateHmac", HMACSwitchCustomerChoice.enabled.name(),
                "inbound.hmacSecret", "",
                "inbound.hmacHeader", "hmac-header",
                "inbound.hmacAlgorithm", "sha_256"));
    WebhookConnectorProperties props = new WebhookConnectorProperties(connectorProps);
    when(registry.containsContextPath(DEFAULT_CONTEXT)).thenReturn(true);
    when(registry.getWebhookConnectorByContextPath(DEFAULT_CONTEXT)).thenReturn(List.of(props));

    ResponseEntity<WebhookResponse> response =
        controller.inbound(DEFAULT_CONTEXT, DEFAULT_REQUEST_BODY, DEFAULT_HEADERS);

    verify(connectorContext).replaceSecrets(props);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getUnauthorizedConnectors()).isNotEmpty();
    assertThat(response.getBody().getUnauthorizedConnectors())
        .contains(props.getConnectorIdentifier());
    assertThat(response.getBody().getExecutedConnectors()).isEmpty();
    assertThat(response.getBody().getUnactivatedConnectors()).isEmpty();
  }

  @Test
  void inbound_ActivationConditionFailed_ReturnsUnactivatedConnectorMessage() throws IOException {
    final String evaluationExpression = "=a=b";
    InboundConnectorProperties connectorProps =
        new InboundConnectorProperties(
            "proc-id",
            1,
            2,
            Map.of(
                "inbound.context",
                DEFAULT_CONTEXT,
                "inbound.activationCondition",
                evaluationExpression,
                "inbound.variableMapping",
                "",
                "inbound.shouldValidateHmac",
                HMACSwitchCustomerChoice.disabled.name()));
    WebhookConnectorProperties props = new WebhookConnectorProperties(connectorProps);
    when(registry.containsContextPath(DEFAULT_CONTEXT)).thenReturn(true);
    when(registry.getWebhookConnectorByContextPath(DEFAULT_CONTEXT)).thenReturn(List.of(props));
    when(feelEngine.evaluate(anyString(), any(Map.class))).thenReturn(false);

    ResponseEntity<WebhookResponse> response =
        controller.inbound(DEFAULT_CONTEXT, DEFAULT_REQUEST_BODY, DEFAULT_HEADERS);

    verify(connectorContext).replaceSecrets(props);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getUnauthorizedConnectors()).isEmpty();
    assertThat(response.getBody().getExecutedConnectors()).isEmpty();
    assertThat(response.getBody().getUnactivatedConnectors()).isNotEmpty();
    assertThat(response.getBody().getUnactivatedConnectors())
        .contains(props.getConnectorIdentifier());
  }

  @Test
  void inbound_ExceptionThrown_ReturnsExceptionConnectorMessage() throws IOException {
    final String evaluationExpression = "=a=b";
    InboundConnectorProperties connectorProps =
        new InboundConnectorProperties(
            "proc-id",
            1,
            2,
            Map.of(
                "inbound.context",
                DEFAULT_CONTEXT,
                "inbound.activationCondition",
                evaluationExpression,
                "inbound.variableMapping",
                "",
                "inbound.shouldValidateHmac",
                HMACSwitchCustomerChoice.disabled.name()));
    WebhookConnectorProperties props = new WebhookConnectorProperties(connectorProps);
    when(registry.containsContextPath(DEFAULT_CONTEXT)).thenReturn(true);
    when(registry.getWebhookConnectorByContextPath(DEFAULT_CONTEXT)).thenReturn(List.of(props));

    final String exceptionMessage = "Something went wrong";
    when(feelEngine.evaluate(anyString(), any(Map.class)))
        .thenThrow(new RuntimeException(exceptionMessage));

    ResponseEntity<WebhookResponse> response =
        controller.inbound(DEFAULT_CONTEXT, DEFAULT_REQUEST_BODY, DEFAULT_HEADERS);

    verify(connectorContext).replaceSecrets(props);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getUnauthorizedConnectors()).isEmpty();
    assertThat(response.getBody().getExecutedConnectors()).isEmpty();
    assertThat(response.getBody().getUnactivatedConnectors()).isEmpty();
    assertThat(response.getBody().getErrors()).isNotEmpty();
    assertThat(response.getBody().getErrors())
        .contains(props.getConnectorIdentifier() + ">" + exceptionMessage);
  }
}
