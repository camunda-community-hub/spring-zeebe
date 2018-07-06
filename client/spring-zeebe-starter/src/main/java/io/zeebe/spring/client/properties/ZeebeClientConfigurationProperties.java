package io.zeebe.spring.client.properties;

import java.time.Duration;

import io.zeebe.client.ZeebeClientConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "zeebe.client")
public class ZeebeClientConfigurationProperties implements ZeebeClientConfiguration
{
    private String brokerContactPoint;

    private int maxRequests;

    private int sendBufferSize;

    private int topicSubscriptionPrefetchCapacity;

    private Duration tcpChannelKeepAlivePeriod;

    private Duration requestTimeout;

    private Duration requestBlocktime;

    private int numManagementThreads;

    private int numSubscriptionExecutionThreads;

    @Override
    public int getDefaultTopicSubscriptionBufferSize()
    {
        throw new UnsupportedOperationException("implement");
    }

    @Override
    public int getDefaultJobSubscriptionBufferSize()
    {
        throw new UnsupportedOperationException("implement");
    }

    @Override
    public String getDefaultJobWorkerName()
    {
        throw new UnsupportedOperationException("implement");
    }

    @Override
    public Duration getDefaultJobTimeout()
    {
        throw new UnsupportedOperationException("implement");
    }

    @Override
    public String getDefaultTopic()
    {
        throw new UnsupportedOperationException("implement");
    }
}
