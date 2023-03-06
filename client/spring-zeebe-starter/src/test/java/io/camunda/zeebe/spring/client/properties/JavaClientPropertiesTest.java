package io.camunda.zeebe.spring.client.properties;

import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(
  properties = {
    "zeebe.client.gateway.address=localhost12345",
    "zeebe.client.job.pollinterval=99s",
    "zeebe.client.worker.name=testName",
  }
)
@ContextConfiguration(classes = JavaClientPropertiesTest.TestConfig.class)
public class JavaClientPropertiesTest {

  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {
    @Bean("jsonMapper")
    @ConditionalOnMissingBean(JsonMapper.class)
    public JsonMapper jsonMapper() {
      return new ZeebeObjectMapper();
    }
  }

  @Autowired
  private ZeebeClientConfigurationProperties properties;

  @Test
  public void hasBrokerContactPoint() throws Exception {
    assertThat(properties.getGatewayAddress()).isEqualTo("localhost12345");
  }

  @Test
  public void hasWorkerName() throws Exception {
    assertThat(properties.getDefaultJobWorkerName()).isEqualTo("testName");
  }

  @Test
  public void hasJobPollInterval() throws Exception {
    assertThat(properties.getJob().getPollInterval()).isEqualTo(Duration.ofSeconds(99));
  }
}
