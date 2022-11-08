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

import io.camunda.connector.runtime.inbound.registry.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.registry.InboundConnectorRegistry;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.search.Sort;
import io.camunda.operate.search.SortOrder;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.instance.Process;
import io.camunda.zeebe.model.bpmn.instance.StartEvent;
import io.camunda.zeebe.model.bpmn.instance.zeebe.ZeebeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@ConditionalOnProperty(name = "camunda.connector.polling.enabled")
public class ProcessDefinitionImporter {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessDefinitionImporter.class);

  private InboundConnectorRegistry registry;
  private CamundaOperateClient camundaOperateClient;

  @Autowired
  public ProcessDefinitionImporter(
      InboundConnectorRegistry registry, CamundaOperateClient camundaOperateClient) {
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
    bpmnModelInstance.getDefinitions().getChildElementsByType(Process.class).stream()
        .flatMap(process -> process.getChildElementsByType(StartEvent.class).stream())
        .map(startEvent -> startEvent.getSingleExtensionElement(ZeebeProperties.class))
        .filter(Objects::nonNull)
        .forEach(zeebeProperties -> processZeebeProperties(processDefinition, zeebeProperties));
    // TODO: Also process intermediate catching message events and Receive Tasks
  }

  private void processZeebeProperties(
      ProcessDefinition processDefinition, ZeebeProperties zeebeProperties) {
    InboundConnectorProperties properties =
        new InboundConnectorProperties(
            processDefinition.getBpmnProcessId(),
            processDefinition.getVersion().intValue(),
            processDefinition.getKey(),
            zeebeProperties.getProperties().stream()
                // Avoid issue with OpenJDK when collecting null values
                // -->
                // https://stackoverflow.com/questions/24630963/nullpointerexception-in-collectors-tomap-with-null-entry-values
                // .collect(Collectors.toMap(ZeebeProperty::getName, ZeebeProperty::getValue)));
                .collect(
                    HashMap::new,
                    (m, zeebeProperty) -> m.put(zeebeProperty.getName(), zeebeProperty.getValue()),
                    HashMap::putAll));

    if (InboundConnectorProperties.TYPE_WEBHOOK.equals(properties.getType())) {

      LOG.debug("Found inbound webhook connector: " + properties);
      registry.registerWebhookConnector(properties);

    } else {

      LOG.warn("Found other connector than webhook, which is not yet supported: " + properties);
      // registry.registerOtherInboundConnector(properties);

    }
  }
}
