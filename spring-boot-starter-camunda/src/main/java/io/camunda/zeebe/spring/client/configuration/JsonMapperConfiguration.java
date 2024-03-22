package io.camunda.zeebe.spring.client.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.common.json.SdkObjectMapper;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonMapperConfiguration {
  private final ObjectMapper objectMapper;

  @Autowired
  public JsonMapperConfiguration(@Autowired(required = false) ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean(name = "zeebeJsonMapper")
  @ConditionalOnMissingBean
  public JsonMapper jsonMapper() {
    if (objectMapper == null) {
      return new ZeebeObjectMapper();
    }
    return new ZeebeObjectMapper(objectMapper);
  }

  @Bean(name = "commonJsonMapper")
  @ConditionalOnMissingBean
  public io.camunda.common.json.JsonMapper commonJsonMapper() {
    if (objectMapper == null) {
      return new SdkObjectMapper();
    }
    return new SdkObjectMapper(objectMapper);
  }
}
