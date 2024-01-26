package io.camunda.zeebe.spring.test.configuration;

import static io.camunda.zeebe.spring.client.CamundaAutoConfiguration.DEFAULT_OBJECT_MAPPER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/** Fallback values if certain beans are missing */
public class ZeebeTestDefaultConfiguration {

  @Bean(name = "zeebeJsonMapper")
  @ConditionalOnMissingBean
  public JsonMapper jsonMapper(ObjectMapper objectMapper) {
    return new ZeebeObjectMapper(objectMapper);
  }

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    return DEFAULT_OBJECT_MAPPER;
  }
}
