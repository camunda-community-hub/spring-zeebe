package io.camunda.zeebe.spring.client.lifecycle;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.ZeebeClientSpringConfiguration;
import org.springframework.beans.factory.ObjectFactory;

/**
 * Factory to create a new ZeebeClient.
 *
 * This is only needed in "normal" applications, not in tests.
 *
 * In normal applications, the @EnableZeebeClient annotation pulls in {@link ZeebeClientSpringConfiguration}
 * which provides a ZeebeClientObjectFactory based on the normal ZeebeClientBuilder
 * configured by Spring properties.
 *
 * This is then used in the {@link ZeebeClientLifecycle} to create a ZeebeClient when ready.
 *
 * In testing, the ZeebeTestClientSpringConfiguration provides a proxy to the ZeebeClient instead, skipping the Lifecycle
 * as engine and  client are tied to the test lifecycle.
 */
public interface ZeebeClientObjectFactory extends ObjectFactory<ZeebeClient> {

}
