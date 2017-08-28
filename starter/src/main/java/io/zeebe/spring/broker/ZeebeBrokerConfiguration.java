package io.zeebe.spring.broker;

import org.springframework.context.annotation.Bean;

public class ZeebeBrokerConfiguration {

    @Bean
    public BrokerLifecycle springBroker() {
        return new BrokerLifecycle();
    }

}
