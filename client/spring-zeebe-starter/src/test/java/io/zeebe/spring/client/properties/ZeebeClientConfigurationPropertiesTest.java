package io.zeebe.spring.client.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"zeebe.client.brokerContactPoint=localhost12345" })
@ContextConfiguration(classes = ZeebeClientConfigurationPropertiesTest.TestConfig.class)
public class ZeebeClientConfigurationPropertiesTest
{
    @EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
    public static class TestConfig
    {
    }


    @Autowired
    private ZeebeClientConfigurationProperties properties;

    @Test
    public void hasBrokerContactPoint() throws Exception
    {
        assertThat(properties.getBrokerContactPoint()).isEqualTo("localhost12345");
    }
}