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
package io.camunda.connector.runtime.inbound.configs;

import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.runtime.inbound.context.InboundConnectorContextImpl;
import io.camunda.connector.runtime.util.feel.FeelEngineWrapper;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalContextBeanConfiguration {

  @Bean
  @ConditionalOnMissingBean
  protected SecretProvider getSecretProvider() {
    Iterator<SecretProvider> secretProviders = ServiceLoader.load(SecretProvider.class).iterator();
    if (!secretProviders.hasNext()) {
      return System::getenv; // Fallback to environment variables loading
    }
    return secretProviders.next();
  }

  @Bean
  public InboundConnectorContext jobHandlerContext(
    final SecretProvider secretProvider,
    final ZeebeClient zeebeClient,
    final FeelEngineWrapper feelEngine) {
    return new InboundConnectorContextImpl(secretProvider, zeebeClient, feelEngine);
  }
}
