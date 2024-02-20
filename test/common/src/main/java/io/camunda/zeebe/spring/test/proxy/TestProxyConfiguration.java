package io.camunda.zeebe.spring.test.proxy;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class TestProxyConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Bean
  public ZeebeClientProxy zeebeClientProxy() {
    return new ZeebeClientProxy();
  }

  @Bean
  @Primary
  public ZeebeClient proxiedZeebeClient(ZeebeClientProxy zeebeClientProxy) {
    return (ZeebeClient)
        Proxy.newProxyInstance(
            this.getClass().getClassLoader(), new Class[] {ZeebeClient.class}, zeebeClientProxy);
  }

  @Bean
  public ZeebeTestEngineProxy zeebeTestEngineProxy() {
    return new ZeebeTestEngineProxy();
  }

  @Bean
  public ZeebeTestEngine proxiedZeebeTestEngine(final ZeebeTestEngineProxy zeebeTestEngineProxy) {
    return (ZeebeTestEngine)
        Proxy.newProxyInstance(
            this.getClass().getClassLoader(),
            new Class[] {ZeebeTestEngine.class},
            zeebeTestEngineProxy);
  }
}
