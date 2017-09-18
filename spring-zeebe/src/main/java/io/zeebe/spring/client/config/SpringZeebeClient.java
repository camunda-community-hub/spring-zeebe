package io.zeebe.spring.client.config;

import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.client.event.ClientStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class SpringZeebeClient extends ZeebeClientImpl implements SmartLifecycle {

    public static final int PHASE = 22222;
    private final ZeebeClientProperties properties;
    private final ApplicationEventPublisher publisher;

    private final Set<Consumer<SpringZeebeClient>> onStart = new LinkedHashSet<>();

    public SpringZeebeClient(final ZeebeClientProperties properties, final ApplicationEventPublisher publisher) {
        super(properties.get());
        this.properties = properties;
        this.publisher = publisher;
        log.info("SpringZeebeClient created");
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isAutoStartup() {
        return properties.isAutoStartup();
    }

    @Override
    public void start() {
        connect();
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

    public SpringZeebeClient onStart(final Consumer<SpringZeebeClient> consumer) {
        onStart.add(consumer);
        return this;
    }

    @Override
    public void stop() {
        this.stop(() -> {});
    }

    @Override
    public boolean isRunning() {
        return isConnected();
    }

    @Override
    public int getPhase() {
        return PHASE;
    }
}
