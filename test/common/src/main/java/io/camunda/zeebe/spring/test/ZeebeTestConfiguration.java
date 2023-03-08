package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.spring.client.AbstractZeebeBaseConfiguration;
import io.camunda.zeebe.spring.client.SpringZeebeAutoConfiguration;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.camunda.zeebe.spring.test.configuration.ZeebeTestDefaultConfiguration;
import io.camunda.zeebe.spring.test.proxy.TestProxyConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
@ImportAutoConfiguration({TestProxyConfiguration.class, ZeebeTestDefaultConfiguration.class})
@AutoConfigureBefore(SpringZeebeAutoConfiguration.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class) // make sure Spring created ObjectMapper is preferred if available
public class ZeebeTestConfiguration extends AbstractZeebeBaseConfiguration {

  @Bean
  public SpringZeebeTestContext enableTestContext() {
    // add marker bean to Spring context that we are running in a test case
    return new SpringZeebeTestContext();
  }

}
