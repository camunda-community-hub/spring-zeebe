package io.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ZeebeClientSpringConfigurationDefaultPropertiesTest.TestConfig.class)
public class ZeebeClientSpringConfigurationDefaultPropertiesTest {

  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {

  }

  @Autowired
  private ZeebeClientConfigurationProperties properties;

  @Test
  public void hasBrokerContactPoint() throws Exception {
    assertThat(properties.getBrokerContactPoint()).isEqualTo("0.0.0.0:26500");
  }

  @Test
  public void hasWorkerName() throws Exception {
    assertThat(properties.getWorker().getName()).isEqualTo("default");

  }

  @Test
  public void hasWorkerTimeout() throws Exception {
    assertThat(properties.getWorker().getTimeout()).isEqualTo(Duration.ofSeconds(300));
  }

  @Test
  public void hasWorkerMaxJobsActive() throws Exception {
    assertThat(properties.getWorker().getMaxJobsActive()).isEqualTo(32);

  }

  @Test
  public void hasWorkerPollInterval() throws Exception {
    assertThat(properties.getWorker().getPollInterval()).isEqualTo(Duration.ofNanos(100000000));
  }

  @Test
  public void hasWorkerThreads() throws Exception {
    assertThat(properties.getWorker().getThreads()).isEqualTo(1);
  }
}
