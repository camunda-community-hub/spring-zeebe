package io.zeebe.spring.client.config;

import io.zeebe.client.TasksClient;
import io.zeebe.client.TopicsClient;
import io.zeebe.client.WorkflowsClient;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.clustering.impl.TopologyResponse;
import io.zeebe.client.cmd.Request;
import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.client.event.ClientStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Spring managed lifecycle implementation of {@link ZeebeClient}.
 * Uses delegate for {@link ZeebeClientImpl} internally.
 */
@Slf4j
public class SpringZeebeClient implements ZeebeClient, SmartLifecycle, Supplier<ZeebeClientImpl> {

    public static final int PHASE = 22222;
    private final ZeebeClientProperties properties;
    private final ApplicationEventPublisher publisher;

    /**
     * Holds list of consumers to be notified after the client was started.
     */
    private final Set<Consumer<ZeebeClient>> onStart = new LinkedHashSet<>();

    /**
     * Late init during {@link #start()}.
     */
    private ZeebeClientImpl client;

    private boolean  hasBeenClosed = false;

    public SpringZeebeClient(final ZeebeClientProperties properties, final ApplicationEventPublisher publisher) {
        this.properties = properties;
        this.publisher = publisher;
        log.info("SpringZeebeClient created");
    }

    @Override
    public boolean isAutoStartup() {
        return properties.isAutoStartup();
    }

    @Override
    public void start() {
        client = new ZeebeClientImpl(properties.get());
        log.info("SpringZeebeClient connected");
        publisher.publishEvent(new ClientStartedEvent());

        onStart.forEach(c -> c.accept(this));
    }

    @Override
    public void stop(final Runnable runnable) {
        close();
        log.info("SpringZeebeClient closed");
        runnable.run();
    }

    public SpringZeebeClient onStart(final Consumer<ZeebeClient> consumer) {
        onStart.add(consumer);
        return this;
    }

    @Override
    public void stop() {
        this.stop(() -> {});
    }

    @Override
    public boolean isRunning() {
        return client != null;
    }

    @Override
    public int getPhase() {
        return PHASE;
    }

    @Override
    public TasksClient tasks() {
        return get().tasks();
    }

    @Override
    public WorkflowsClient workflows() {
        return get().workflows();
    }

    @Override
    public TopicsClient topics() {
        return get().topics();
    }

    @Override
    public Request<TopologyResponse> requestTopology() {
        return get().requestTopology();
    }

    @Override
    public void disconnect() {
        get().disconnect();
    }

    @Override
    public void close() {
        if (!hasBeenClosed) {
            get().close();
            hasBeenClosed = true;
        }
    }


    @Override
    public ZeebeClientImpl get() {
        if (!isRunning()) {
            throw new IllegalStateException("client is not running!");
        }
        return client;
    }
}
