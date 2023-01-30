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
package io.camunda.connector.runtime.inbound.importer;

import io.camunda.connector.api.inbound.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorRegistry;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.search.Sort;
import io.camunda.operate.search.SortOrder;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "camunda.connector.polling.enabled")
public class ProcessDefinitionImporter {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessDefinitionImporter.class);

  private final InboundConnectorRegistry registry;
  private final CamundaOperateClient camundaOperateClient;

  @Autowired
  public ProcessDefinitionImporter(
    InboundConnectorRegistry registry,
    CamundaOperateClient camundaOperateClient) {
    this.registry = registry;
    this.camundaOperateClient = camundaOperateClient;
  }

  @Scheduled(fixedDelayString = "${camunda.connector.polling.interval:5000}")
  public void scheduleImport() throws OperateException {
    LOG.trace("Query process deployments...");

    SearchQuery processDefinitionQuery =
      new SearchQuery.Builder().withSort(new Sort("version", SortOrder.ASC)).build();

    List<ProcessDefinition> processDefinitions =
      camundaOperateClient.searchProcessDefinitions(processDefinitionQuery);

    if (processDefinitions==null) {
      LOG.trace("... returned no process definitions.");
      return;
    }
    LOG.trace("... returned " + processDefinitions.size() + " process definitions.");

    for (ProcessDefinition processDefinition : processDefinitions) {

      if (!registry.processDefinitionChecked(processDefinition.getKey())) {
        LOG.debug("Check " + processDefinition + " for connectors.");
        registry.markProcessDefinitionChecked(
          processDefinition.getKey(),
          processDefinition.getBpmnProcessId(),
          processDefinition.getVersion().intValue());

        String processDefinitionXml =
          camundaOperateClient.getProcessDefinitionXml(processDefinition.getKey());
        processBpmnXml(processDefinition, processDefinitionXml);
      }
    }

    // Make sure all webhooks endpoints are properly set
    registry.rewireWebhookEndpoints();
  }

  private void processBpmnXml(ProcessDefinition processDefinition, String resource) {
    final BpmnModelInstance bpmnModelInstance =
      Bpmn.readModelFromStream(new ByteArrayInputStream(resource.getBytes()));
    ProcessDefinitionInspector discoverer =
      new ProcessDefinitionInspector(processDefinition, bpmnModelInstance);

    discoverer.findInboundConnectors().forEach(this::processInboundConnectorProperties);
  }

  private void processInboundConnectorProperties(InboundConnectorProperties properties) {
    if (InboundConnectorProperties.TYPE_WEBHOOK.equals(properties.getType())) {

      LOG.info("Found inbound webhook connector: " + properties);
      registry.registerWebhookConnector(properties);

    } else {

      LOG.warn("Found other connector than webhook, which is not yet supported: " + properties);
      // registry.registerOtherInboundConnector(properties);

    }
  }
}
