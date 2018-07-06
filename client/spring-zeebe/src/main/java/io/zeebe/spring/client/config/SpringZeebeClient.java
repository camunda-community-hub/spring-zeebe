package io.zeebe.spring.client.config;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.ZeebeClientConfiguration;
import io.zeebe.client.api.clients.TopicClient;
import io.zeebe.client.api.commands.CreateTopicCommandStep1;
import io.zeebe.client.api.commands.TopicsRequestStep1;
import io.zeebe.client.api.commands.TopologyRequestStep1;
import io.zeebe.client.api.record.ZeebeObjectMapper;
import io.zeebe.client.api.subscription.ManagementSubscriptionBuilderStep1;
import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.client.event.ClientStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;

/**
 * Spring managed lifecycle implementation of {@link ZeebeClient}.
 * Uses delegate for {@link ZeebeClientImpl} internally.
 */
@Slf4j
public class SpringZeebeClient implements ZeebeClient, SmartLifecycle, Supplier<ZeebeClientImpl>
{

    public static final int PHASE = 22222;
    private final ZeebeClientBuilder builder;
    private final ApplicationEventPublisher publisher;

    /**
     * Holds list of consumers to be notified after the client was started.
     */
    private final Set<Consumer<ZeebeClient>> onStart = new LinkedHashSet<>();

    /**
     * Late init during {@link #start()}.
     */
    private ZeebeClientImpl client;

    private boolean hasBeenClosed = false;

    public SpringZeebeClient(final ZeebeClientBuilder builder, final ApplicationEventPublisher publisher, CreateDefaultTopic createDefaultTopic)
    {
        this.builder = builder;
        this.publisher = publisher;

        onStart(createDefaultTopic);
        log.info("SpringZeebeClient created");
    }

    @Override
    public boolean isAutoStartup()
    {
        return true;
    }

    @Override
    public void start()
    {
        client = (ZeebeClientImpl) builder.build();
        log.info("SpringZeebeClient connected");
        publisher.publishEvent(new ClientStartedEvent());

        onStart.forEach(c -> c.accept(this));
    }

    @Override
    public void stop(final Runnable runnable)
    {
        close();
        log.info("SpringZeebeClient closed");
        runnable.run();
    }

    public SpringZeebeClient onStart(final Consumer<ZeebeClient> consumer)
    {
        onStart.add(consumer);
        return this;
    }

    @Override
    public void stop()
    {
        this.stop(() -> {
        });
    }

    @Override
    public boolean isRunning()
    {
        return client != null;
    }

    @Override
    public int getPhase()
    {
        return PHASE;
    }


    @Override
    public TopicClient topicClient(String topicName)
    {
        return get().topicClient();
    }

    @Override
    public TopicClient topicClient()
    {
        return get().topicClient();
    }

    @Override
    public ZeebeObjectMapper objectMapper()
    {
        return get().objectMapper();
    }

    @Override
    public CreateTopicCommandStep1 newCreateTopicCommand()
    {
        return get().newCreateTopicCommand();
    }

    @Override
    public TopicsRequestStep1 newTopicsRequest()
    {
        return get().newTopicsRequest();
    }

    @Override
    public TopologyRequestStep1 newTopologyRequest()
    {
        return get().newTopologyRequest();
    }

    @Override
    public ManagementSubscriptionBuilderStep1 newManagementSubscription()
    {
        return get().newManagementSubscription();
    }

    @Override
    public ZeebeClientConfiguration getConfiguration()
    {
        return get().getConfiguration();
    }

    @Override
    public void close()
    {
        if (!hasBeenClosed)
        {
            get().close();
            hasBeenClosed = true;
        }
    }


    @Override
    public ZeebeClientImpl get()
    {
        if (!isRunning())
        {
            throw new IllegalStateException("client is not running!");
        }
        return client;
    }
}
