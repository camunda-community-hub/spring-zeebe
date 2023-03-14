package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.spring.client.CamundaAutoConfiguration;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.camunda.zeebe.spring.test.configuration.ZeebeTestDefaultConfiguration;
import io.camunda.zeebe.spring.test.proxy.TestProxyConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
@ImportAutoConfiguration({
  TestProxyConfiguration.class,
  ZeebeTestDefaultConfiguration.class,
  CamundaAutoConfiguration.class
})
@AutoConfigureBefore(CamundaAutoConfiguration.class)
public class CamundaTestAutoConfiguration {

  @Bean
  public SpringZeebeTestContext enableTestContext() {
    // add marker bean to Spring context that we are running in a test case
    return new SpringZeebeTestContext();
  }

}
