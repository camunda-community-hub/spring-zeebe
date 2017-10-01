package io.zeebe.spring.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "zeebe.client")
public class ZeebeClientConfigurationProperties implements ZeebeClientProperties {

    private String brokerContactPoint;

    private String maxRequests;

    private String sendBufferSize;

    private String threadingMode;

    private String taskExecutionThreads;

    private String topicSubscriptionPrefetchCapacity;

    private String tcpChannelKeepAlivePeriod;
    
}
