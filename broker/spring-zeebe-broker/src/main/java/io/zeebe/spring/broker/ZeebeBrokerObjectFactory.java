package io.zeebe.spring.broker;

import io.zeebe.broker.Broker;
import io.zeebe.spring.util.ZeebeObjectFactory;

/**
 * How to create a new {@link Broker} instance.
 */
@FunctionalInterface
public interface ZeebeBrokerObjectFactory extends ZeebeObjectFactory<Broker> {

}
