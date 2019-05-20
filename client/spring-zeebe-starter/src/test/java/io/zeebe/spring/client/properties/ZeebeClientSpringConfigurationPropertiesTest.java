package io.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(
  properties = {
    "zeebe.client.broker.contactPoint=localhost12345",
    "zeebe.client.worker.name=testName",
    "zeebe.client.worker.timeout=99s",
    "zeebe.client.worker.maxJobsActive=99",
    "zeebe.client.worker.pollInterval=99s",
    "zeebe.client.worker.threads=99"
  }
)
@ContextConfiguration(classes = ZeebeClientSpringConfigurationPropertiesTest.TestConfig.class)
public class ZeebeClientSpringConfigurationPropertiesTest {

  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {

  }

  @Autowired
  private ZeebeClientConfigurationProperties properties;

  @Test
  public void hasBrokerContactPoint() throws Exception {
    assertThat(properties.getBrokerContactPoint()).isEqualTo("localhost12345");
  }

  @Test
  public void hasWorkerName() throws Exception {
    assertThat(properties.getWorker().getName()).isEqualTo("testName");

  }

  @Test
  public void hasWorkerTimeout() throws Exception {
    assertThat(properties.getWorker().getTimeout()).isEqualTo(Duration.ofSeconds(99));
  }

  @Test
  public void hasWorkerMaxJobsActive() throws Exception {
    assertThat(properties.getWorker().getMaxJobsActive()).isEqualTo(99);

  }

  @Test
  public void hasWorkerPollInterval() throws Exception {
    assertThat(properties.getWorker().getPollInterval()).isEqualTo(Duration.ofSeconds(99));
  }

  @Test
  public void hasWorkerThreads() throws Exception {
    assertThat(properties.getWorker().getThreads()).isEqualTo(99);
  }
}
