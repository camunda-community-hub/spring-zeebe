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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorProperties;
import io.camunda.connector.api.inbound.InboundConnectorResult;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorRegistry;
import io.camunda.connector.runtime.inbound.signature.HMACAlgoCustomerChoice;
import io.camunda.connector.runtime.inbound.signature.HMACSignatureValidator;
import io.camunda.connector.runtime.inbound.signature.HMACSwitchCustomerChoice;
import io.camunda.connector.runtime.util.feel.FeelEngineWrapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.metrics.MetricsRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@ConditionalOnProperty("camunda.connector.webhook.enabled")
public class InboundWebhookRestController {

  private static final Logger LOG = LoggerFactory.getLogger(InboundWebhookRestController.class);

  private final InboundConnectorRegistry registry;
  private final InboundConnectorContext connectorContext;
  private final ZeebeClient zeebeClient;
  private final FeelEngineWrapper feelEngine;
  private final ObjectMapper jsonMapper;
  private final MetricsRecorder metricsRecorder;

  @Autowired
  public InboundWebhookRestController(
    final InboundConnectorRegistry registry,
    final InboundConnectorContext connectorContext,
    final ZeebeClient zeebeClient,
    final FeelEngineWrapper feelEngine,
    final ObjectMapper jsonMapper, MetricsRecorder metricsRecorder) {
    this.registry = registry;
    this.connectorContext = connectorContext;
    this.zeebeClient = zeebeClient;
    this.feelEngine = feelEngine;
    this.jsonMapper = jsonMapper;
    this.metricsRecorder = metricsRecorder;
  }

  @PostMapping("/inbound/{context}")
  public ResponseEntity<WebhookResponse> inbound(
      @PathVariable String context,
      @RequestBody byte[] bodyAsByteArray, // required to calculate HMAC
      @RequestHeader Map<String, String> headers)
      throws IOException {

    LOG.debug("Received inbound hook on {}", context);

    if (!registry.containsContextPath(context)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "No webhook found for context: " + context);
    }
    metricsRecorder.increase(MetricsRecorder.METRIC_NAME_INBOUND_CONNECTOR, MetricsRecorder.ACTION_ACTIVATED , InboundConnectorProperties.TYPE_WEBHOOK);

    // TODO(nikku): what context do we expose?
    // TODO(igpetrov): handling exceptions? Throw or fail? Maybe spring controller advice?
    // TODO: Check if that always works (can we have an empty body for example?)
    Map bodyAsMap = jsonMapper.readValue(bodyAsByteArray, Map.class);

    HashMap<String, Object> request = new HashMap<>();
    request.put("body", bodyAsMap);
    request.put("headers", headers);
    final Map<String, Object> webhookContext = Collections.singletonMap("request", request);

    WebhookResponse response = new WebhookResponse();
    Collection<WebhookConnectorProperties> connectors =
        registry.getWebhookConnectorByContextPath(context);
    for (WebhookConnectorProperties connectorProperties : connectors) {
      connectorContext.replaceSecrets(connectorProperties);

      try {
        if (!isValidHmac(connectorProperties, bodyAsByteArray, headers)) {
          LOG.debug("HMAC validation failed {} :: {}", context, webhookContext);
          response.addUnauthorizedConnector(connectorProperties);
        } else { // Authorized
          if (!activationConditionTriggered(connectorProperties, webhookContext)) {
            LOG.debug("Should not activate {} :: {}", context, webhookContext);
            response.addUnactivatedConnector(connectorProperties);
          } else {
            Map<String, Object> variables = extractVariables(connectorProperties, webhookContext);
            InboundConnectorResult result = connectorContext
              .correlate(connectorProperties.getCorrelationPoint(), variables);

            LOG.debug(
                "Webhook {} created process instance {}",
                connectorProperties,
                result);

            response.addExecutedConnector(connectorProperties, result);
          }
        }
      } catch (Exception exception) {
        LOG.error("Webhook {} failed to create process instance", connectorProperties, exception);
        metricsRecorder.increase(MetricsRecorder.METRIC_NAME_INBOUND_CONNECTOR, MetricsRecorder.ACTION_FAILED , InboundConnectorProperties.TYPE_WEBHOOK);
        response.addException(connectorProperties, exception);
      }
    }

    metricsRecorder.increase(MetricsRecorder.METRIC_NAME_INBOUND_CONNECTOR, MetricsRecorder.ACTION_COMPLETED , InboundConnectorProperties.TYPE_WEBHOOK);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  private boolean isValidHmac(
      final WebhookConnectorProperties connectorProperties,
      final byte[] bodyAsByteArray,
      final Map<String, String> headers)
      throws NoSuchAlgorithmException, InvalidKeyException {
    if (HMACSwitchCustomerChoice.disabled
        .name()
        .equals(connectorProperties.getShouldValidateHmac())) {
      return true;
    }

    HMACSignatureValidator validator =
        new HMACSignatureValidator(
            bodyAsByteArray,
            headers,
            connectorProperties.getHmacHeader(),
            connectorProperties.getHmacSecret(),
            HMACAlgoCustomerChoice.valueOf(connectorProperties.getHmacAlgorithm()));

    return validator.isRequestValid();
  }

  private Map<String, Object> extractVariables(
          WebhookConnectorProperties connectorProperties, Map<String, Object> context) {

    String variableMapping = connectorProperties.getVariableMapping();
    if (variableMapping == null) {
      return context;
    }
    return feelEngine.evaluate(variableMapping, context);
    //      throw fail("Failed to extract variables", connectorProperties, exception);
  }

  private boolean activationConditionTriggered(
          WebhookConnectorProperties connectorProperties, Map<String, Object> context) {

    // at this point we assume secrets exist / had been specified
    String activationCondition = connectorProperties.getActivationCondition();
    if (activationCondition == null || activationCondition.trim().length()==0) {
      return true;
    }
    Object shouldActivate = feelEngine.evaluate(activationCondition, context);
    return Boolean.TRUE.equals(shouldActivate);
    //      throw fail("Failed to check activation", connectorProperties, exception);
  }
}
