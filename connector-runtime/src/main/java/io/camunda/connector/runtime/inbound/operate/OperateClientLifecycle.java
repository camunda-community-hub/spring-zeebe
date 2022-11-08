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
package io.camunda.connector.runtime.inbound.operate;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.FlownodeInstance;
import io.camunda.operate.dto.Incident;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.dto.Variable;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import org.apache.hc.core5.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Lifecycle implementation that also directly acts as a CamundaOperateClient by delegating all
 * methods to the CamundaOperateClient that is controlled (and kept in the delegate field)
 */
@Component
public class OperateClientLifecycle extends CamundaOperateClient
    implements SmartLifecycle, Supplier<CamundaOperateClient> {

  public static final int PHASE = 22222;
  // Do not auto startup on bean creation - but rather when the client is really used
  protected boolean autoStartup = false;
  protected boolean running = false;
  protected boolean runningInTestContext = false;

  protected final OperateClientFactory factory;
  protected CamundaOperateClient delegate;

  @Autowired
  public OperateClientLifecycle(final OperateClientFactory factory) {
    this.factory = factory;
  }

  /** Allows to set the delegate being used manually, helpful for test cases */
  public OperateClientLifecycle(final CamundaOperateClient delegate) {
    this.factory = null;
    this.delegate = delegate;
  }

  @Override
  public void start() {
    if (factory != null) {
      try {
        delegate = factory.camundaOperateClient();
      } catch (OperateException e) {
        throw new RuntimeException("Could not start Camunda Operate Client: " + e.getMessage(), e);
      }
      this.running = true;
    } else {
      // in test cases we have injected a delegate already
      runningInTestContext = true;
    }
  }

  @Override
  public void stop() {
    try {
      delegate = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      running = false;
    }
  }

  @Override
  public CamundaOperateClient get() {
    if (!isRunning()) {
      // lazy initialize client
      start();
    }
    return delegate;
  }

  @Override
  public boolean isAutoStartup() {
    return autoStartup;
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public int getPhase() {
    return PHASE;
  }

  @Override
  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return get().getProcessDefinition(key);
  }

  @Override
  public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query)
      throws OperateException {
    return get().searchProcessDefinitions(query);
  }

  @Override
  public String getProcessDefinitionXml(Long key) throws OperateException {
    return get().getProcessDefinitionXml(key);
  }

  @Override
  public BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException {
    return get().getProcessDefinitionModel(key);
  }

  @Override
  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return get().getProcessInstance(key);
  }

  @Override
  public List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
    return get().searchProcessInstances(query);
  }

  @Override
  public FlownodeInstance getFlownodeInstance(Long key) throws OperateException {
    return get().getFlownodeInstance(key);
  }

  @Override
  public List<FlownodeInstance> searchFlownodeInstances(SearchQuery query) throws OperateException {
    return get().searchFlownodeInstances(query);
  }

  @Override
  public Incident getIncident(Long key) throws OperateException {
    return get().getIncident(key);
  }

  @Override
  public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    return get().searchIncidents(query);
  }

  @Override
  public Variable getVariable(Long key) throws OperateException {
    return get().getVariable(key);
  }

  @Override
  public List<Variable> searchVariables(SearchQuery query) throws OperateException {
    return get().searchVariables(query);
  }

  @Override
  public String getOperateUrl() {
    return get().getOperateUrl();
  }

  @Override
  public void setOperateUrl(String operateUrl) {
    get().setOperateUrl(operateUrl);
  }

  @Override
  public Header getAuthHeader() {
    return get().getAuthHeader();
  }

  @Override
  public void setAuthHeader(Header authHeader) {
    get().setAuthHeader(authHeader);
  }

  @Override
  public void setTokenExpiration(int tokenExpiration) {
    get().setTokenExpiration(tokenExpiration);
  }
}
