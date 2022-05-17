package io.camunda.zeebe.spring.client.factory;

import org.springframework.beans.factory.ObjectFactory;

/**
 * {@link ObjectFactory} for zeebe Broker
 */
public interface ZeebeObjectFactory<T extends AutoCloseable> extends ObjectFactory<T> {

}
