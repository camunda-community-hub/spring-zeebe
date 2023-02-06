/* Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
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

import io.camunda.connector.api.annotation.Secret;
import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import io.camunda.connector.impl.inbound.InboundConnectorProperties;
import io.camunda.connector.runtime.inbound.signature.HMACSwitchCustomerChoice;

public class WebhookConnectorProperties {

  private final InboundConnectorProperties genericProperties;
  @Secret private String context;
  private String activationCondition;
  private String variableMapping;
  private String shouldValidateHmac;
  @Secret private String hmacSecret;
  @Secret private String hmacHeader;
  private String hmacAlgorithm;

  public WebhookConnectorProperties(InboundConnectorProperties properties) {
    this.genericProperties = properties;

    this.context = readPropertyRequired("inbound.context");
    this.activationCondition = readPropertyNullable("inbound.activationCondition");
    this.variableMapping = readPropertyNullable("inbound.variableMapping");
    this.shouldValidateHmac = readPropertyWithDefault("inbound.shouldValidateHmac",  HMACSwitchCustomerChoice.disabled.name());
    this.hmacSecret = readPropertyNullable("inbound.hmacSecret");
    this.hmacHeader = readPropertyNullable("inbound.hmacHeader");
    this.hmacAlgorithm = readPropertyNullable("inbound.hmacAlgorithm");
  }

  public String getConnectorIdentifier() {
    return ""
      + genericProperties.getType()
      + "-"
      + getContext()
      + "-"
      + genericProperties.getBpmnProcessId()
      + "-"
      + genericProperties.getVersion();
  }

  protected String readPropertyWithDefault(String propertyName, String defaultValue) {
    return genericProperties
      .getProperties()
      .getOrDefault(propertyName, defaultValue);

  }

  protected String readPropertyNullable(String propertyName) {
    return genericProperties.getProperties().get(propertyName);
  }

  protected String readPropertyRequired(String propertyName) {
    String result = readPropertyNullable(propertyName);
    if (result == null) {
      throw new IllegalArgumentException(
        "Property '" + propertyName + "' must be set for connector");
    }
    return result;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getActivationCondition() {
    return activationCondition;
  }

  public void setActivationCondition(String activationCondition) {
    this.activationCondition = activationCondition;
  }

  public String getVariableMapping() {
    return variableMapping;
  }

  public void setVariableMapping(String variableMapping) {
    this.variableMapping = variableMapping;
  }

  // Dropdown that indicates whether customer wants to validate webhook request with HMAC. Values:
  // enabled | disabled
  public String getShouldValidateHmac() {
    return shouldValidateHmac;
  }

  public void setShouldValidateHmac(String shouldValidateHmac) {
    this.shouldValidateHmac = shouldValidateHmac;
  }

  // HMAC secret token. An arbitrary String, example 'mySecretToken'
  public String getHmacSecret() {
    return hmacSecret;
  }

  public void setHmacSecret(String hmacSecret) {
    this.hmacSecret = hmacSecret;
  }

  // Indicates which header is used to store HMAC signature. Example, X-Hub-Signature-256
  public String getHmacHeader() {
    return hmacHeader;
  }

  public void setHmacHeader(String hmacHeader) {
    this.hmacHeader = hmacHeader;
  }

  // Indicates which algorithm was used to produce HMAC signature. Should correlate enum names of
  // io.camunda.connector.inbound.security.signature.HMACAlgoCustomerChoice
  public String getHmacAlgorithm() {
    return hmacAlgorithm;
  }

  public void setHmacAlgorithm(String hmacAlgorithm) {
    this.hmacAlgorithm = hmacAlgorithm;
  }

  public String getType() {
    return genericProperties.getType();
  }

  public ProcessCorrelationPoint getCorrelationPoint() {
    return genericProperties.getCorrelationPoint();
  }

  public String getBpmnProcessId() {
    return genericProperties.getBpmnProcessId();
  }

  public int getProcessDefinitionVersion() {
    return genericProperties.getVersion();
  }

  public long getProcessDefinitionKey() {
    return genericProperties.getProcessDefinitionKey();
  }

  @Override
  public String toString() {
    return "WebhookConnectorProperties-" + genericProperties.toString();
  }
}
