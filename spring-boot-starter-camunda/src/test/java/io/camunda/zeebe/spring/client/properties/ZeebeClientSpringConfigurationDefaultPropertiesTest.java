package io.camunda.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = ZeebeClientSpringConfigurationDefaultPropertiesTest.TestConfig.class)
public class ZeebeClientSpringConfigurationDefaultPropertiesTest {

  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {}

  @Autowired private ZeebeClientConfigurationProperties properties;

  @Test
  public void hasGatewayAddress() throws Exception {
    assertThat(properties.getGatewayAddress()).isEqualTo("0.0.0.0:26500");
  }

  @Test
  public void hasRequestTimeout() throws Exception {
    assertThat(properties.getRequestTimeout()).isEqualTo(Duration.ofSeconds(10));
  }

  @Test
  public void hasNoWorkerName() throws Exception {
    assertThat(properties.getDefaultJobWorkerName()).isNull();
  }

  @Test
  public void hasJobTimeout() throws Exception {
    assertThat(properties.getDefaultJobTimeout()).isEqualTo(Duration.ofSeconds(300));
  }

  @Test
  public void hasWorkerMaxJobsActive() throws Exception {
    assertThat(properties.getDefaultJobWorkerMaxJobsActive()).isEqualTo(32);
  }

  @Test
  public void hasJobPollInterval() throws Exception {
    assertThat(properties.getDefaultJobPollInterval()).isEqualTo(Duration.ofNanos(100000000));
  }

  @Test
  public void hasWorkerThreads() throws Exception {
    assertThat(properties.getNumJobWorkerExecutionThreads()).isEqualTo(1);
  }

  @Test
  public void hasMessageTimeToLeave() throws Exception {
    assertThat(properties.getDefaultMessageTimeToLive()).isEqualTo(Duration.ofSeconds(3600));
  }

  @Test
  public void isSecurityPlainTextDisabled() throws Exception {
    assertThat(properties.isPlaintextConnectionEnabled()).isFalse();
  }

  @Test
  public void hasSecurityCertificatePath() throws Exception {
    assertThat(properties.getCaCertificatePath()).isNull();
  }
}
