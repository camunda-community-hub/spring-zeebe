package io.camunda.zeebe.spring.client.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.common.json.SdkObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.CamundaAutoConfiguration;
import io.camunda.zeebe.spring.client.configuration.ZeebeClientProdAutoConfiguration;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

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
    })
@ContextConfiguration(
    classes = {
      ZeebeClientStarterAutoConfigurationCustomJsonMapperTest.TestConfig.class,
      CamundaAutoConfiguration.class
    })
public class ZeebeClientStarterAutoConfigurationCustomJsonMapperTest {

  @Autowired private io.camunda.zeebe.client.api.JsonMapper jsonMapper;
  @Autowired private ZeebeClientProdAutoConfiguration autoConfiguration;
  @Autowired private ApplicationContext applicationContext;

  @Test
  void getJsonMapper() {
    assertThat(jsonMapper).isNotNull();
    assertThat(autoConfiguration).isNotNull();

    Map<String, io.camunda.zeebe.client.api.JsonMapper> jsonMapperBeans =
        applicationContext.getBeansOfType(io.camunda.zeebe.client.api.JsonMapper.class);
    Object objectMapper = ReflectionTestUtils.getField(jsonMapper, "objectMapper");

    assertThat(jsonMapperBeans.size()).isEqualTo(2);
    assertThat(jsonMapperBeans.containsKey("overridingJsonMapper")).isTrue();
    assertThat(jsonMapperBeans.get("overridingJsonMapper")).isSameAs(jsonMapper);
    assertThat(jsonMapperBeans.containsKey("aSecondJsonMapper")).isTrue();
    assertThat(jsonMapperBeans.get("aSecondJsonMapper")).isNotSameAs(jsonMapper);
    assertThat(objectMapper).isNotNull();
    assertThat(objectMapper).isInstanceOf(ObjectMapper.class);
    assertThat(((ObjectMapper) objectMapper).getDeserializationConfig()).isNotNull();
    assertThat(
            ((ObjectMapper) objectMapper)
                .getDeserializationConfig()
                .isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES))
        .isTrue();
    assertThat(
            ((ObjectMapper) objectMapper)
                .getDeserializationConfig()
                .isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))
        .isTrue();
  }

  @Test
  void testBuilder() {
    // ZeebeClientBuilder builder = autoConfiguration.builder(jsonMapper, Collections.emptyList());
    // assertThat(builder).isNotNull();

    ZeebeClient client = applicationContext.getBean(ZeebeClient.class);
    final io.camunda.zeebe.client.api.JsonMapper clientJsonMapper =
        AopTestUtils.getUltimateTargetObject(client.getConfiguration().getJsonMapper());
    assertThat(clientJsonMapper).isSameAs(jsonMapper);
    assertThat(clientJsonMapper).isSameAs(applicationContext.getBean("overridingJsonMapper"));
    assertThat(client.getConfiguration().getGatewayAddress()).isEqualTo("localhost12345");
    assertThat(client.getConfiguration().getDefaultRequestTimeout())
        .isEqualTo(Duration.ofSeconds(99));
    assertThat(client.getConfiguration().getCaCertificatePath()).isEqualTo("aPath");
    assertThat(client.getConfiguration().isPlaintextConnectionEnabled()).isTrue();
    assertThat(client.getConfiguration().getDefaultJobWorkerMaxJobsActive()).isEqualTo(99);
    assertThat(client.getConfiguration().getDefaultJobPollInterval())
        .isEqualTo(Duration.ofSeconds(99));
  }

  public static class TestConfig {
    @Bean
    public io.camunda.common.json.JsonMapper commonJsonMapper() {
      return new SdkObjectMapper();
    }

    @Primary
    @Bean(name = "overridingJsonMapper")
    public io.camunda.zeebe.client.api.JsonMapper zeebeJsonMapper() {
      ObjectMapper objectMapper =
          new ObjectMapper()
              .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true)
              .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
      return new ZeebeObjectMapper(objectMapper);
    }

    @Bean(name = "aSecondJsonMapper")
    public io.camunda.zeebe.client.api.JsonMapper aSecondJsonMapper() {
      ObjectMapper objectMapper =
          new ObjectMapper()
              .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true)
              .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
      return new ZeebeObjectMapper(objectMapper);
    }

    @Bean(name = "jsonMapper")
    public ZeebeClientStarterAutoConfigurationCustomJsonMapperTest.JsonMapper jsonMapper() {
      return new ZeebeClientStarterAutoConfigurationCustomJsonMapperTest.JsonMapper();
    }
  }

  private static class JsonMapper {}
}
