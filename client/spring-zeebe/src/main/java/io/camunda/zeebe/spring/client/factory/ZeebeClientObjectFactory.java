package io.camunda.zeebe.spring.client.factory;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.ObjectFactory;

/**
 * Factory to create a new ZeebeClient.
 *
 * There are different implementations of this factory depending on the context
 *
 * In normal applications, the @EnableZeebeClient annotation pulls in {@link io.camunda.zeebe.spring.client.config.ZeebeClientSpringConfiguration}, which provides a ZeebeClientObjectFactory based on the normal ZeebeClientBuilder configured by Spring properties
 *
 * In testing, the ZeebeTestClientSpringConfiguration provides the ZeebeClientObjectFactory by providing the ZeebeClient obtained from the ZeebeTestEngine.
 */
@FunctionalInterface
public interface ZeebeClientObjectFactory extends ObjectFactory<ZeebeClient> {

}
