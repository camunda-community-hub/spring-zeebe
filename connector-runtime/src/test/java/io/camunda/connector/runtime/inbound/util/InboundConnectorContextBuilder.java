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
package io.camunda.connector.runtime.inbound.util;

import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.runtime.inbound.context.InboundJobHandlerContext;
import io.camunda.connector.runtime.util.feel.FeelEngineWrapper;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.HashMap;
import java.util.Map;

public class InboundConnectorContextBuilder {
  protected final Map<String, String> secrets = new HashMap<>();
  protected SecretProvider secretProvider = secrets::get;
  protected ZeebeClient zeebeClient;
  protected FeelEngineWrapper feelEngine = new FeelEngineWrapper();

  public static InboundConnectorContextBuilder create() {
    return new InboundConnectorContextBuilder();
  }

  /**
   * Provides the secret's value for the given name.
   *
   * @param name - the secret's name, e.g. MY_SECRET when referred to as "secrets.MY_SECRET"
   * @param value - the secret's value
   * @return builder for fluent API
   */
  public InboundConnectorContextBuilder secret(String name, String value) {
    secrets.put(name, value);
    return this;
  }

  /**
   * Provides the secret values via the defined {@link SecretProvider}.
   *
   * @param secretProvider - provider for secret values, given a secret name
   * @return builder for fluent API
   */
  public InboundConnectorContextBuilder secrets(SecretProvider secretProvider) {
    this.secretProvider = secretProvider;
    return this;
  }

  public InboundConnectorContextBuilder zeebeClient(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
    return this;
  }

  public InboundConnectorContextBuilder feelEngine(FeelEngineWrapper feelEngine) {
    this.feelEngine = feelEngine;
    return this;
  }

  /**
   * @return the {@link InboundConnectorContext} including all
   *     previously defined properties
   */
  public InboundJobHandlerContext build() {
    return new InboundJobHandlerContext(secretProvider, zeebeClient, feelEngine);
  }
}
