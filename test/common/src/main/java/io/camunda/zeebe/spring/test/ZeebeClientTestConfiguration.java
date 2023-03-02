package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.AbstractZeebeBaseClientSpringConfiguration;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.camunda.zeebe.spring.test.proxy.TestProxyConfiguration;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({TestProxyConfiguration.class})
public class ZeebeClientTestConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public SpringZeebeTestContext enableTestContext() {
    // add marker bean to Spring context that we are running in a test case
    return new SpringZeebeTestContext();
  }

}
