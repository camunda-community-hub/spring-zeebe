package io.zeebe.spring.spike;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class Client implements SmartLifecycle {

    public static final int PHASE = 10000;

    private final ClientConfiguration configuration;

    public Client(ClientConfiguration configuration) {
        log.info("Client()");
        this.configuration = configuration;
    }

    private boolean running;

    @PostConstruct
    void postConstruct() {
        log.info("Client#postConstruct");
    }

    @PreDestroy
    void preDestroy() {
        log.info("Client#preDestroy");
    }

    @Override
    public boolean isAutoStartup() {
        log.info("Client#isAutoStartup {}", configuration.isAutoStartup());
        return configuration.isAutoStartup();
    }

    @Override
    public void start() {
        log.info("Client#start()");
    }

    private void stop(String msg) {
        log.info(msg);
        running = false;
    }

    @Override
    public void stop() {
        stop("client#stop(runnable)");
    }

    @Override
    public void stop(Runnable runnable) {
        stop("client#stop(runnable)");
        runnable.run();
    }

    @Override
    public boolean isRunning() {
        log.info("client is running: {}", running);
        return running;
    }

    @Override
    public int getPhase() {
        log.info("Client#getPhase() {}", PHASE);
        return PHASE;
    }
}
