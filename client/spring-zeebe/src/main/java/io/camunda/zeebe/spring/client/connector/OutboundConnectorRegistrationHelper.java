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

import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.impl.ConnectorUtil;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Configuration class holding information of a connector. */
public class OutboundConnectorRegistrationHelper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundConnectorRegistrationHelper.class);

  /** Pattern describing the connector env configuration pattern. */
  public static final Pattern CONNECTOR_FUNCTION_PATTERN =
      Pattern.compile("^CONNECTOR_(.*)_FUNCTION$");

  public static Map<String, String> hardwiredEnvironmentVariables;

  public static void addHardwiredEnvironmentVariable(String key, String value) {
    if (hardwiredEnvironmentVariables == null) {
      hardwiredEnvironmentVariables = new HashMap<>();
    }
    hardwiredEnvironmentVariables.put(key, value);
  }

  public static void clearHardwiredEnvironmentVariable() {
    hardwiredEnvironmentVariables = null;
  }

  public static Map<String, String> getEnvironmentVariables() {
    if (hardwiredEnvironmentVariables != null) {
      HashMap<String, String> result = new HashMap<>();
      result.putAll(System.getenv());
      result.putAll(hardwiredEnvironmentVariables);
      return result;
    }
    return System.getenv();
  }

  public static boolean isEnvConfigured() {
    return getEnvironmentVariables().entrySet().stream()
        .anyMatch(entry -> CONNECTOR_FUNCTION_PATTERN.matcher(entry.getKey()).matches());
  }

  /**
   * Parses the connector registrations from the environment.
   *
   * @return the list of registrations
   */
  public static List<OutboundConnectorConfiguration> parse() {
    if (isEnvConfigured()) {
      LOGGER.info("Reading environment variables to find connectors that are not Spring beans");
      return parseFromEnv();
    } else {
      LOGGER.info("Parsing SPI to find connectors that are not Spring beans");
      return parseFromSPI();
    }
  }

  /**
   * Parses connectors registered via SPI ("auto discovery")
   *
   * @return the list of registrations
   */
  public static List<OutboundConnectorConfiguration> parseFromSPI() {
    ArrayList<OutboundConnectorConfiguration> result = new ArrayList<>();

    Iterator<OutboundConnectorFunction> functionIterator = ServiceLoader.load(OutboundConnectorFunction.class).iterator();
    while (functionIterator.hasNext()) {
      OutboundConnectorFunction function = functionIterator.next();
      Optional<OutboundConnectorConfiguration> outboundConnectorConfiguration = ConnectorUtil.getOutboundConnectorConfiguration(function.getClass());
      if (!outboundConnectorConfiguration.isPresent()) {
        throw new RuntimeException(
          String.format(
            "OutboundConnectorFunction %s is missing @OutboundConnector annotation",
            function.getClass()));
      }
      OutboundConnectorConfiguration cfg = outboundConnectorConfiguration.get();
      result.add(new OutboundConnectorConfiguration(
        cfg.getName(), cfg.getType(), cfg.getInputVariables(), function));
    }
    return result;
  }

  /**
   * Parses the connector registrations configured through via environment variables.
   *
   * @return the list of registrations
   */
  public static List<OutboundConnectorConfiguration> parseFromEnv() {

    List<OutboundConnectorConfiguration> connectors = new ArrayList<OutboundConnectorConfiguration>();

    for (Map.Entry<String, String> entry : getEnvironmentVariables().entrySet()) {

      String key = entry.getKey();

      Matcher match = CONNECTOR_FUNCTION_PATTERN.matcher(key);

      if (match.matches()) {
        connectors.add(parseConnector(match.group(1)));
      }
    }

    return connectors;
  }

  private static OutboundConnectorConfiguration parseConnector(final String name) {

    OutboundConnectorFunction function =
        getEnv(name, "FUNCTION")
            .map(OutboundConnectorRegistrationHelper::loadConnectorFunction)
            .orElseThrow(() -> envMissing("No function specified", name, "FUNCTION"));

    Optional<OutboundConnectorConfiguration> config = ConnectorUtil.getOutboundConnectorConfiguration(function.getClass());

    if (!config.isPresent()) {
      LOGGER.warn(
          "OutboundConnectorFunction {} is missing @OutboundConnector annotation",
          function.getClass().getName());
    }

    Optional<String> type = getEnv(name, "TYPE");
    if (!type.isPresent()) {
      if (config.isPresent()) {
        type = Optional.of(config.get().getType());
      }
    }
    if (!type.isPresent()) {
      throw envMissing("Type not specified", name, "TYPE");
    }

    String[] inputVariables = null;
    Optional<String> inputVariablesString = getEnv(name, "INPUT_VARIABLES");
    if (inputVariablesString.isPresent()) {
      inputVariables = inputVariablesString.get().split(",");
    } else {
      if (config.isPresent()) {
        inputVariables = config.get().getInputVariables();
      }
    }
    if (inputVariables==null) {
      throw envMissing("Variables not specified", name, "INPUT_VARIABLES");
    }
    return new OutboundConnectorConfiguration(name, type.get(), inputVariables, function);
  }

  private static Optional<String> getEnv(final String name, final String detail) {
    return Optional.ofNullable(getEnvironmentVariables().get("CONNECTOR_" + name + "_" + detail));
  }

  @SuppressWarnings("unchecked")
  private static OutboundConnectorFunction loadConnectorFunction(String clsName) {

    try {
      Class<OutboundConnectorFunction> cls = (Class<OutboundConnectorFunction>) Class.forName(clsName);

      return cls.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException
        | ClassCastException
        | NoSuchMethodException e) {
      throw loadFailed("Failed to load " + clsName, e);
    }
  }

  private static RuntimeException loadFailed(String s, Exception e) {
    return new IllegalStateException(s, e);
  }

  private static RuntimeException envMissing(String message, String name, String envKey) {
    return new RuntimeException(
        String.format(
            "%s: Please configure it via CONNECTOR_%s_%s environment variable",
            message, name, envKey));
  }
}
