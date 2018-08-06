package io.zeebe.spring.client;

import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.util.ZeebeObjectFactory;

@FunctionalInterface
public interface ZeebeClientObjectFactory extends ZeebeObjectFactory<ZeebeClientImpl> {

}
