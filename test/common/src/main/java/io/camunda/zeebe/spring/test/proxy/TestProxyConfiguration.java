package io.camunda.zeebe.spring.test.proxy;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Proxy;

public class TestProxyConfiguration {

  @Bean
  public ZeebeClientProxy zeebeClientProxy(final ZeebeTestEngine zeebeTestEngine,
                                           @Autowired(required = false) final JsonMapper jsonMapper,
                                           final ZeebeAnnotationProcessorRegistry zeebeAnnotationProcessorRegistry) {
    final ZeebeClientProxy zeebeClientProxy = new ZeebeClientProxy();
    final ZeebeClientBuilder zeebeClientBuilder = ZeebeClient.newClientBuilder().gatewayAddress(zeebeTestEngine.getGatewayAddress()).usePlaintext();
    if (jsonMapper != null) {
      zeebeClientBuilder.withJsonMapper(jsonMapper);
    }
    final ZeebeClient zeebeClient = zeebeClientBuilder.build();
    zeebeClientProxy.swapZeebeClient(zeebeClient);
    return zeebeClientProxy;
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
  public ZeebeTestEngine proxiedZeebeTestEngine(final ZeebeTestEngineProxy zeebeTestEngineProxy) {
    return (ZeebeTestEngine) Proxy.newProxyInstance(
      this.getClass().getClassLoader(),
      new Class[] { ZeebeTestEngine.class },
      zeebeTestEngineProxy);
  }
}
