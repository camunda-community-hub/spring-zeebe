package io.zeebe.spring.broker.config;

import org.springframework.context.annotation.Bean;

/**
 * Included by {@link io.zeebe.spring.broker.EnableZeebeBroker} annotation.
 */
public class ZeebeBrokerConfiguration
{

    @Bean
    public BrokerLifecycle springBroker()
    {
        return new BrokerLifecycle();
    }

}
