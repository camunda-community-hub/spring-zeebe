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
package io.camunda.zeebe.spring.client.jobhandling.copy;

import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.api.secret.SecretStore;
import io.camunda.connector.impl.outbound.AbstractOutboundConnectorContext;
import io.camunda.zeebe.client.api.response.ActivatedJob;

/**
 * TODO: Copied from connector-runtime till https://github.com/camunda/connector-sdk/pull/136 is merged
 */
public class JobHandlerContext extends AbstractOutboundConnectorContext {

  private final ActivatedJob job;
  private SecretStore secretStore;

  public JobHandlerContext(final ActivatedJob job, SecretStore secretStore) {
    this.job = job;
    this.secretStore = secretStore;
  }

  @Override
  public SecretStore getSecretStore() {
    return secretStore;
  }

  @Override
  public <T> T getVariablesAsType(Class<T> cls) {
    return job.getVariablesAsType(cls);
  }

  @Override
  public String getVariables() {
    return job.getVariables();
  }
}
