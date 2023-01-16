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
package io.camunda.connector.runtime.inbound.registry;

import io.camunda.connector.api.inbound.InboundConnectorProperties;
import io.camunda.connector.api.inbound.InboundConnectorTarget;
import io.camunda.connector.runtime.inbound.webhook.WebhookConnectorProperties;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class WebhookConnectorRegistry {

  private Set<Long> registeredProcessDefinitionKeys = new HashSet<>();
  private Map<String, List<WebhookConnectorProperties>> registeredWebhookConnectorsByContextPath =
      new HashMap<>();
  private Map<String, Set<WebhookConnectorProperties>> sortedWebhookConnectorsByBpmnId =
      new HashMap<>();
  private Map<String, TreeSet<Integer>> processDefinitionVersionsByBpmnId = new HashMap<>();
  private boolean webhookRegistrationDirty = true;

  /**
   * Reset registry and forget about all connectors, especially useful in tests when the context
   * needs to get cleared
   */
  public void reset() {
    registeredProcessDefinitionKeys = new HashSet<>();
    registeredWebhookConnectorsByContextPath = new HashMap<>();
  }

  public boolean processDefinitionChecked(long processDefinitionKey) {
    return registeredProcessDefinitionKeys.contains(processDefinitionKey);
  }

  public void markProcessDefinitionChecked(
      long processDefinitionKey, String bpmnProcessId, Integer version) {
    registeredProcessDefinitionKeys.add(processDefinitionKey);

    if (!processDefinitionVersionsByBpmnId.containsKey(bpmnProcessId)) {
      processDefinitionVersionsByBpmnId.put(bpmnProcessId, new TreeSet<>());
    }
    processDefinitionVersionsByBpmnId.get(bpmnProcessId).add(version);
    webhookRegistrationDirty = true;
  }

  private boolean hasLatestVersion(String bpmnProcessId) {
    return (processDefinitionVersionsByBpmnId.containsKey(bpmnProcessId));
  }

  private Integer getLatestVersion(String bpmnProcessId) {
    return processDefinitionVersionsByBpmnId.get(bpmnProcessId).last();
  }

  public void registerWebhookConnector(InboundConnectorProperties properties) {
    // markProcessDefinitionChecked(properties.getProcessDefinitionKey(),
    // properties.getBpmnProcessId(), properties.getVersion());
    WebhookConnectorProperties webhookConnectorProperties =
        new WebhookConnectorProperties(properties);

    InboundConnectorTarget connectorTarget = properties.getConnectorTarget();
    String bpmnId = connectorTarget.getBpmnProcessId();

    if (!sortedWebhookConnectorsByBpmnId.containsKey(bpmnId)) {
      sortedWebhookConnectorsByBpmnId.put(
          bpmnId, new TreeSet<>(new WebhookConnectorPropertyComparator()));
    }
    sortedWebhookConnectorsByBpmnId.get(bpmnId).add(webhookConnectorProperties);
    webhookRegistrationDirty = true;
  }

  public void rewireWebhookEndpoints() {
    Map<String, List<WebhookConnectorProperties>> newWebhooks = new HashMap<>();

    for (String bpmnId : sortedWebhookConnectorsByBpmnId.keySet()) {
      Set<WebhookConnectorProperties> connectorsForBpmnId =
          sortedWebhookConnectorsByBpmnId.get(bpmnId);
      WebhookConnectorProperties lastConnector = null;

      // Wire all endpoints for a specific Bpmn ID
      Map<String, WebhookConnectorProperties> candidatesByContext = new HashMap<>();

      for (WebhookConnectorProperties props : connectorsForBpmnId) {
        // Connectors are sorted by version ascending
        candidatesByContext.put(props.getContext(), props);
        // If an older version was already registered for the same context path
        // the mapping is overwritten / replaced (the newer process will be started)

        lastConnector = props;
      }

      // Now check if the webhook was removed in a later version
      // which disables this activation
      if (hasLatestVersion(bpmnId) && getLatestVersion(bpmnId) > lastConnector.getConnectorTarget().getVersion()) {
        candidatesByContext.remove(lastConnector.getContext());
      }

      // Now we are done and can add the remaining candidates
      for (WebhookConnectorProperties props : candidatesByContext.values()) {
        putWebhookEndpoint(newWebhooks, props);
      }
    }

    webhookRegistrationDirty = false;
    // Replace existing registrations with latest list - do this in one call to avoid
    // multi-threading issues
    registeredWebhookConnectorsByContextPath = newWebhooks;
  }

  private static void putWebhookEndpoint(
      Map<String, List<WebhookConnectorProperties>> webhookMap,
      WebhookConnectorProperties webhookConnectorProperties) {
    String context = webhookConnectorProperties.getContext();
    if (!webhookMap.containsKey(context)) {
      // list because there can be multiple processes that are started for one context
      // from different process definitions
      webhookMap.put(context, new ArrayList<>());
    }
    webhookMap.get(context).add(webhookConnectorProperties);
  }

  public boolean containsContextPath(String context) {
    return getRegisteredWebhookConnectorsByContextPath().containsKey(context);
  }

  public Collection<WebhookConnectorProperties> getWebhookConnectorByContextPath(String context) {
    return getRegisteredWebhookConnectorsByContextPath().get(context);
  }

  private Map<String, List<WebhookConnectorProperties>>
      getRegisteredWebhookConnectorsByContextPath() {
    if (webhookRegistrationDirty) {
      rewireWebhookEndpoints();
    }
    return registeredWebhookConnectorsByContextPath;
  }

  public void registerOtherInboundConnector(InboundConnectorProperties properties) {
    // registeredInboundConnectors.add(properties);
    // Now all known connectors on the classpath need to be known
    // Somehow the type of the connector must resolve to either a
    // PollingInboundConnectorFunction function1 = null;
    // SubscriptionInboundConnector function2 = null;
    // Then this runtime will either start a Subscription or some polling component
    // TODO: Will be addded at a later state
  }
}
