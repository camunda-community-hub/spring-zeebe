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

import java.util.Map;

public class InboundConnectorProperties {

  public static String TYPE_WEBHOOK = "webhook";
  public static String TYPE_SUBSCRIPTION = "subscription";
  // public static String TYPE_POLLING = "polling";

  private final String type;
  private final Map<String, String> properties;
  /* Fields used if the connector should start a process instance */
  private final long processDefinitionKey;
  private final String bpmnProcessId;
  private final int version;
  /* Fields used if the connector should correlate a message
  private String messageName;
  private String correlationKey;
  private String messageId;
   */

  public InboundConnectorProperties(
      String bpmnProcessId,
      int version,
      long processDefinitionKey,
      Map<String, String> properties) {
    this.type = properties.get("inbound.type");
    this.bpmnProcessId = bpmnProcessId;
    this.version = version;
    this.properties = properties;
    this.processDefinitionKey = processDefinitionKey;
  }

  /**
   * @return a string identifying this connector in log message or responses
   */
  public String getConnectorIdentifier() {
    return type + "-" + bpmnProcessId + "-" + version;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public int getVersion() {
    return version;
  }

  public String getType() {
    return type;
  }

  public long getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  @Override
  public String toString() {
    return "ConnectorProperties{"
        + "type='"
        + type
        + '\''
        + ", processDefinitionKey="
        + processDefinitionKey
        + ", bpmnProcessId='"
        + bpmnProcessId
        + '\''
        + ", version="
        + version
        + ", properties="
        + properties
        + '}';
  }
}
