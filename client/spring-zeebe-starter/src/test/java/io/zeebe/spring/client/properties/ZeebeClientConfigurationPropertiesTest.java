package io.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"zeebe.client.broker.contactPoint=localhost12345"})
@ContextConfiguration(classes = ZeebeClientConfigurationPropertiesTest.TestConfig.class)
public class ZeebeClientConfigurationPropertiesTest {
  @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
  public static class TestConfig {}

  @Autowired private ZeebeClientConfigurationProperties properties;

  @Test
  public void hasBrokerContactPoint() throws Exception {
    assertThat(properties.getBrokerContactPoint()).isEqualTo("localhost12345");
  }
}
