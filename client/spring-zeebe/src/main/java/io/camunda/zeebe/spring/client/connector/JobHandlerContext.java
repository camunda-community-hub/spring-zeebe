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
package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.secret.SecretStore;
import io.camunda.connector.impl.context.AbstractConnectorContext;
import io.camunda.zeebe.client.api.response.ActivatedJob;

/**
 * Implementation of {@link OutboundConnectorContext} passed on to
 * a {@link io.camunda.connector.api.outbound.OutboundConnectorFunction} when called from the {@link
 * ConnectorJobHandler}.
 */
public class JobHandlerContext extends AbstractConnectorContext
    implements OutboundConnectorContext {

  private final ActivatedJob job;

  public JobHandlerContext(final ActivatedJob job, final SecretStore secretStore) {
    super(secretStore);
    this.job = job;
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
