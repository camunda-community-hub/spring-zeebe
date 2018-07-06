package io.zeebe.spring.client.config;

import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
@Configuration
@RequiredArgsConstructor
public class ZeebeClientStarterAutoConfiguration
{

    private final ZeebeClientConfigurationProperties configurationProperties;

    @Bean
    @Primary
    public ZeebeClientBuilder builder()
    {
        final ZeebeClientBuilderImpl builder = new ZeebeClientBuilderImpl();

        return builder;
    }
}
