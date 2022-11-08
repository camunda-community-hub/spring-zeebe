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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.secret.SecretProvider;
import io.camunda.connector.api.secret.SecretStore;
import io.camunda.zeebe.spring.client.connector.InboundJobHandlerContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.ServiceLoader;

@Configuration
public class LocalContextBeanConfiguration {

  @Bean
  public ObjectMapper jacksonMapper() {
    return new ObjectMapper();
  }

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
  @ConditionalOnMissingBean
  public SecretStore secretStore(final SecretProvider secretProvider) {
    return new SecretStore(secretProvider);
  }

  @Bean
  public InboundConnectorContext jobHandlerContext(final SecretStore secretStore) {
    return new InboundJobHandlerContext(secretStore);
  }
}
