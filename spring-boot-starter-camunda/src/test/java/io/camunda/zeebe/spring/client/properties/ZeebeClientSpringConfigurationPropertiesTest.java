package io.camunda.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
@ContextConfiguration(classes = ZeebeClientSpringConfigurationPropertiesTest.TestConfig.class)
public class ZeebeClientSpringConfigurationPropertiesTest {

  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {
    @Bean("jsonMapper")
    @ConditionalOnMissingBean(JsonMapper.class)
    public JsonMapper jsonMapper() {
      return new ZeebeObjectMapper();
    }
  }

  @Autowired private ZeebeClientConfigurationProperties properties;

  @Autowired private JsonMapper jsonMapper;

  @Test
  public void hasBrokerContactPoint() throws Exception {
    assertThat(properties.getGatewayAddress()).isEqualTo("localhost12345");
  }

  @Test
  public void hasRequestTimeout() throws Exception {
    assertThat(properties.getRequestTimeout()).isEqualTo(Duration.ofSeconds(99));
  }

  @Test
  public void hasWorkerName() throws Exception {
    assertThat(properties.getDefaultJobWorkerName()).isEqualTo("testName");
  }

  @Test
  public void hasWorkerType() throws Exception {
    assertThat(properties.getDefaultJobWorkerType()).isEqualTo("testType");
  }

  @Test
  public void hasJobTimeout() throws Exception {
    assertThat(properties.getJob().getTimeout()).isEqualTo(Duration.ofSeconds(99));
  }

  @Test
  public void hasWorkerMaxJobsActive() throws Exception {
    assertThat(properties.getWorker().getMaxJobsActive()).isEqualTo(99);
  }

  @Test
  public void hasJobPollInterval() throws Exception {
    assertThat(properties.getJob().getPollInterval()).isEqualTo(Duration.ofSeconds(99));
  }

  @Test
  public void hasWorkerThreads() throws Exception {
    assertThat(properties.getWorker().getThreads()).isEqualTo(99);
  }

  @Test
  public void hasMessageTimeToLeave() throws Exception {
    assertThat(properties.getMessage().getTimeToLive()).isEqualTo(Duration.ofSeconds(99));
  }

  @Test
  public void isSecurityPlainTextDisabled() throws Exception {
    assertThat(properties.getSecurity().isPlaintext()).isTrue();
  }

  @Test
  public void hasSecurityCertificatePath() throws Exception {
    assertThat(properties.getSecurity().getCertPath()).isEqualTo("aPath");
  }

  @Test
  void shouldFooWorkerDisabled() {
    assertThat(properties.getWorker().getOverride().get("foo").getEnabled()).isFalse();
  }
}
