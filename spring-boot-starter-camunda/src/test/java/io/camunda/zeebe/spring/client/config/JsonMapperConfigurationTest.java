package io.camunda.zeebe.spring.client.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import io.camunda.zeebe.spring.client.configuration.JsonMapperConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JacksonAutoConfiguration.class,JsonMapperConfiguration.class})
public class JsonMapperConfigurationTest {

  @Autowired
  private io.camunda.zeebe.client.api.JsonMapper zeebeJsonMapper;

  @Autowired
  private JsonMapper commonJsonMapper;

  @Test
  public void shouldSerializeNullValuesInJson() throws JsonProcessingException {
    // given
    final Map<String, Object> map = new HashMap<>();
    map.put("key", null);
    map.put("key2", "value2");
    // when
    final JsonNode zeebeJsonNode = new ObjectMapper().readTree(zeebeJsonMapper.toJson(map));
    final JsonNode commonJsonNode = new ObjectMapper().readTree(commonJsonMapper.toJson(map));

    // then
    assertThat(zeebeJsonNode.get("key")).isNotNull();
    assertThat(commonJsonNode.get("key")).isNull();

    assertThat(zeebeJsonNode.get("key2").asText()).isEqualTo("value2");
    assertThat(commonJsonNode.get("key2").asText()).isEqualTo("value2");
  }
}
