package io.zeebe.spring.client.config;

import io.zeebe.client.clustering.impl.BrokerPartitionState;
import java.util.function.Consumer;

import io.zeebe.client.ZeebeClient;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@Getter
@ToString
@Slf4j
public class CreateDefaultTopic implements Consumer<ZeebeClient>
{
    @Value("${zeebe.defaultTopic.name:0}")
    private String name;

    @Value("${zeebe.defaultTopic.partitions:-1}")
    private Integer partitions;

    @Value("${zeebe.defaultTopic.create:false}")
    private Boolean create;

    @Override
    public void accept(final ZeebeClient client)
    {
        if (create && !StringUtils.isEmpty(name) && partitions > 0 && !topicExists(client, name))
        {
            client.topics().create(name, partitions).execute();
            log.info("create topic: {}", this);
        }
    }

    private boolean topicExists(final ZeebeClient client, final String topicName)
    {
        return
            client.requestTopology()
                .execute()
                .getBrokers()
                .stream()
                .flatMap(broker -> broker.getPartitions().stream().map(BrokerPartitionState::getTopicName))
                .anyMatch(topicName::equals);
    }

}
