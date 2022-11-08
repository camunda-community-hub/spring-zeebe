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
package io.camunda.zeebe.spring.client.connector.inbound;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import io.camunda.connector.api.secret.SecretStore;
import io.camunda.zeebe.spring.client.connector.InboundJobHandlerContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InboundJobHandlerContextTest {

  private InboundJobHandlerContext testObject;
  @Mock private SecretStore secretStore;

  @BeforeEach
  void beforeEach() {
    testObject = new InboundJobHandlerContext(secretStore);
  }

  @Test
  void constructor_NormalInitialization() {
    Assertions.assertThat(testObject.getSecretStore()).isSameAs(secretStore);
  }

  @Test
  void constructor_InitializationWithNullSecretStore_RaisesException() {
    assertThrowsExactly(RuntimeException.class, () -> new InboundJobHandlerContext(null));
  }
}
