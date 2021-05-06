package io.camunda.spring.client;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.ZeebeClientImpl;
import io.camunda.spring.util.ZeebeObjectFactory;

@FunctionalInterface
public interface ZeebeClientObjectFactory extends ZeebeObjectFactory<ZeebeClient> {

}
