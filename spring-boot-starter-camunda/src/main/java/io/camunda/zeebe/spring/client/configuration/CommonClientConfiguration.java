package io.camunda.zeebe.spring.client.configuration;

import io.camunda.commons.auth.Authentication;
import io.camunda.zeebe.spring.client.properties.CommonClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;

@EnableConfigurationProperties(CommonClientConfigurationProperties.class)
public class CommonClientConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Bean
  public Authentication authentication(CommonClientConfigurationProperties props) {
    return props.getAuthentication();
  }

}
