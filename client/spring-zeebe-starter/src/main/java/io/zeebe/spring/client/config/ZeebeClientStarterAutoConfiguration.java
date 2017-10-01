package io.zeebe.spring.client.config;

import io.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.zeebe.spring.client.properties.ZeebeClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
@Configuration
public class ZeebeClientStarterAutoConfiguration {

    @Autowired
    private ZeebeClientConfigurationProperties configurationProperties;

    @Bean
    @Primary
    public ZeebeClientProperties zeebeClientProperties() {
        return configurationProperties;
    }
}
