package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;

/**
 * How to create a new {@link Broker} instance.
 */
@FunctionalInterface
public interface ZeebeBrokerFactory {

  /**
   * @return the new broker instance
   */
  Broker create();
}
