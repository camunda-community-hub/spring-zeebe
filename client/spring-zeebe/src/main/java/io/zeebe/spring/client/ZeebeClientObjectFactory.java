package io.zeebe.spring.client;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.util.ZeebeObjectFactory;

@FunctionalInterface
public interface ZeebeClientObjectFactory extends ZeebeObjectFactory<ZeebeClient> {

}
