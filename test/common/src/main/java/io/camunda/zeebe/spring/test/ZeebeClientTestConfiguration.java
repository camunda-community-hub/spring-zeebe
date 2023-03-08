package io.camunda.zeebe.spring.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.AbstractZeebeBaseClientSpringConfiguration;
import io.camunda.zeebe.spring.client.SpringZeebeAutoConfiguration;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.camunda.zeebe.spring.test.proxy.TestProxyConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
@ImportAutoConfiguration({TestProxyConfiguration.class, ZeebeTestDefaultConfiguration.class})
@AutoConfigureBefore(SpringZeebeAutoConfiguration.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class) // make sure Spring created ObjectMapper is preferred if available
public class ZeebeClientTestConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  @Bean
  public SpringZeebeTestContext enableTestContext() {
    // add marker bean to Spring context that we are running in a test case
    return new SpringZeebeTestContext();
  }

}
