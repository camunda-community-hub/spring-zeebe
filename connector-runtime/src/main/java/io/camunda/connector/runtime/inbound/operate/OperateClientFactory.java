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
import io.camunda.operate.auth.AuthInterface;
import io.camunda.operate.auth.SaasAuthentication;
import io.camunda.operate.auth.SelfManagedAuthentication;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.exception.OperateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperateClientFactory {

  private static final Logger LOG = LoggerFactory.getLogger(OperateClientFactory.class);

  /**
   * This whole block should be moved into some kind of springified Operate Client (waiting for
   * OpenAPI.json in dependency). Maybe even add client to spring-zeebe itself?
   */
  // Normal Zeebe Engine Properties
  @Value("${zeebe.client.cloud.cluster-id:#{null}}")
  private String clusterId;

  @Value("${zeebe.client.cloud.region:bru-2}")
  private String region;

  @Value("${zeebe.client.cloud.client-id:#{null}}")
  private String clientId;

  @Value("${zeebe.client.cloud.client-secret:#{null}}")
  private String clientSecret;

  // Specific properties to overwrite for Operate
  @Value("${camunda.operate.client.client-id:#{null}}")
  private String operateClientId;

  @Value("${camunda.operate.client.client-secret:#{null}}")
  private String operateClientSecret;

  @Value("${camunda.operate.client.url:#{null}}")
  private String operateUrl;

  @Value("${camunda.operate.client.username:#{null}}")
  private String operateUsername;

  @Value("${camunda.operate.client.password:#{null}}")
  private String operatePassword;

  @Value("${camunda.operate.client.keycloak-url:#{null}}")
  private String operateKeycloakUrl;

  @Value("${camunda.operate.client.keycloak-realm:#{null}}")
  private String operateKeycloakRealm;

  private String getOperateUrl() {
    if (clusterId != null) {
      String url = "https://" + region + ".operate.camunda.io/" + clusterId + "/";
      LOG.debug("Connecting to Camunda Operate SaaS via URL: " + url);
      return url;
    } else if (operateUrl != null) {
      LOG.debug("Connecting to Camunda Operate on URL: " + operateUrl);
      return operateUrl;
    }
    throw new IllegalArgumentException(
        "In order to connect to Camunda Operate you need to specify either a SaaS clusterId or an Operate URL.");
  }

  public AuthInterface getAuthentication(String operateUrl) {
    if (operateKeycloakUrl != null) {
      if (operateClientId != null) {
        LOG.debug("Authenticating with Camunda Operate using Keycloak on " + operateKeycloakUrl);
        return new SelfManagedAuthentication(operateClientId, operateClientSecret)
            .keycloakUrl(operateKeycloakUrl)
            .keycloakRealm(operateKeycloakRealm);
      } else if (clientId != null) {
        LOG.debug("Authenticating with Camunda Operate using Keycloak on " + operateKeycloakUrl);
        return new SelfManagedAuthentication(clientId, clientSecret)
            .keycloakUrl(operateKeycloakUrl)
            .keycloakRealm(operateKeycloakRealm);
      }
    } else {
      if (operateClientId != null) {
        LOG.debug("Authenticating with Camunda Operate using client id and secret");
        return new SaasAuthentication(operateClientId, operateClientSecret);
      } else if (clientId != null) {
        LOG.debug("Authenticating with Camunda Operate using client id and secret");
        return new SaasAuthentication(clientId, clientSecret);
      } else if (operateUsername != null) {
        LOG.debug("Authenticating with Camunda Operate using username and password");
        return new SimpleAuthentication("demo", "demo", operateUrl);
      }
    }
    throw new IllegalArgumentException(
        "In order to connect to Camunda Operate you need to configure authentication properly.");
  }

  public CamundaOperateClient camundaOperateClient() throws OperateException {
    String operateUrl = getOperateUrl();
    return new CamundaOperateClient.Builder()
        .operateUrl(operateUrl)
        .authentication(getAuthentication(operateUrl))
        .build();
  }
}
