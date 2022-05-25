package io.camunda.zeebe.spring.test.proxy;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Proxy;

public class TestProxyConfiguration {

  @Bean
  public ZeebeClientProxy zeebeClientProxy() {
    return new ZeebeClientProxy();
  }

  @Bean
  @Primary
  public ZeebeClient proxiedZeebeClient(ZeebeClientProxy zeebeClientProxy) {
    return (ZeebeClient) Proxy.newProxyInstance(
      this.getClass().getClassLoader(),
      new Class[] { ZeebeClient.class },
      zeebeClientProxy);
  }

  @Bean
  public ZeebeTestEngineProxy zeebeTestEngineProxy() {
    return new ZeebeTestEngineProxy();
  }

  @Bean
  public ZeebeTestEngine proxiedZeebeTestEngine(final ZeebeTestEngineProxy zeebeTestEngineProxy) {
    return (ZeebeTestEngine) Proxy.newProxyInstance(
      this.getClass().getClassLoader(),
      new Class[] { ZeebeTestEngine.class },
      zeebeTestEngineProxy);
  }
}
