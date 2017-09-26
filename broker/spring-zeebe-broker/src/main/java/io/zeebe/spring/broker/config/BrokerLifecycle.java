package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import org.springframework.context.SmartLifecycle;

public class BrokerLifecycle implements SmartLifecycle {

    public static final int PHASE = 1000;

    private Broker broker;

    @Override
    public void start() {
        broker = new Broker((String) null);
    }

    @Override
    public boolean isRunning() {
        return broker != null;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(final Runnable callback) {
        broker.close();
        callback.run();
    }

    @Override
    public void stop() {
        this.stop(() -> {
        });
    }

    @Override
    public int getPhase() {
        return PHASE;
    }
}
