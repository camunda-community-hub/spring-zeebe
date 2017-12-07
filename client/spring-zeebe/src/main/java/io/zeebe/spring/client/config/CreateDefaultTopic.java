package io.zeebe.spring.client.config;

import java.util.function.Consumer;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.clustering.impl.TopicLeader;
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
        if (create && !StringUtils.isEmpty(name) && partitions > 0 && !client.requestTopology().execute().getTopicLeaders().stream().map(TopicLeader::getTopicName).anyMatch(t -> name.equals(t)))
        {
            client.topics().create(name, partitions).execute();
            log.info("create topic: {}", this);
        }
    }
}
