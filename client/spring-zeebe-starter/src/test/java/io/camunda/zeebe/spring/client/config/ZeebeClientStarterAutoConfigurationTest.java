package io.camunda.zeebe.spring.client.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.api.JsonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
  properties = {
    "zeebe.client.broker.gatewayAddress=localhost12345",
    "zeebe.client.requestTimeout=99s",
    "zeebe.client.job.timeout=99s",
    "zeebe.client.job.pollInterval=99s",
    "zeebe.client.worker.maxJobsActive=99",
    "zeebe.client.worker.threads=99",
    "zeebe.client.worker.defaultName=testName",
    "zeebe.client.worker.defaultType=testType",
    "zeebe.client.worker.override.foo.enabled=false",
    "zeebe.client.message.timeToLive=99s",
    "zeebe.client.security.certpath=aPath",
    "zeebe.client.security.plaintext=true"
  }
)
@ContextConfiguration(classes = { ZeebeClientStarterAutoConfiguration.class, ZeebeClientStarterAutoConfigurationTest.TestConfig.class })
public class ZeebeClientStarterAutoConfigurationTest {

  public static class TestConfig {
    @Bean
    public ZeebeClient zeebeClient() {
      return ZeebeClient.newClient();
    }
  }

  @Autowired
  private JsonMapper jsonMapper;
  @Autowired
  private ZeebeClientStarterAutoConfiguration autoConfiguration;
  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void getJsonMapper() {
    assertThat(jsonMapper).isNotNull();
    assertThat(autoConfiguration).isNotNull();

    Map<String, JsonMapper> jsonMapperBeans = applicationContext.getBeansOfType(JsonMapper.class);
    Object objectMapper = ReflectionTestUtils.getField(jsonMapper, "objectMapper");

    assertThat(jsonMapperBeans.size()).isEqualTo(1);
    assertThat(jsonMapperBeans.containsKey("zeebeJsonMapper")).isTrue();
    assertThat(jsonMapperBeans.get("zeebeJsonMapper")).isSameAs(jsonMapper);
    assertThat(objectMapper).isNotNull();
    assertThat(objectMapper).isInstanceOf(ObjectMapper.class);
    assertThat(((ObjectMapper)objectMapper).getDeserializationConfig()).isNotNull();
    assertThat(((ObjectMapper)objectMapper).getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)).isFalse();
    assertThat(((ObjectMapper)objectMapper).getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)).isFalse();
  }

  @Test
  void testBuilder() {
    ZeebeClientBuilder builder = autoConfiguration.builder(jsonMapper, Collections.emptyList());

    assertThat(builder).isNotNull();

    ZeebeClient client = builder.build();
    assertThat(client.getConfiguration().getJsonMapper()).isSameAs(jsonMapper);
    assertThat(client.getConfiguration().getGatewayAddress()).isEqualTo("localhost12345");
    assertThat(client.getConfiguration().getDefaultRequestTimeout()).isEqualTo(Duration.ofSeconds(99));
    assertThat(client.getConfiguration().getCaCertificatePath()).isEqualTo("aPath");
    assertThat(client.getConfiguration().isPlaintextConnectionEnabled()).isTrue();
    assertThat(client.getConfiguration().getDefaultJobWorkerMaxJobsActive()).isEqualTo(99);
    assertThat(client.getConfiguration().getDefaultJobPollInterval()).isEqualTo(Duration.ofSeconds(99));
  }
}
