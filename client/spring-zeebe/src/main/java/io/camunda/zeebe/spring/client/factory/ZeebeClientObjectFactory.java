package io.camunda.zeebe.spring.client.factory;

import io.camunda.zeebe.client.ZeebeClient;

@FunctionalInterface
public interface ZeebeClientObjectFactory extends ZeebeObjectFactory<ZeebeClient> {

}
