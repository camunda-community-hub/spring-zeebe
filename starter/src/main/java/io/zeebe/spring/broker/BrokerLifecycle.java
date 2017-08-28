package io.zeebe.spring.broker;

import io.zeebe.broker.Broker;
import org.springframework.context.SmartLifecycle;

public class BrokerLifecycle implements SmartLifecycle {

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
    public void stop(Runnable _unused) {
        stop();
    }

    @Override
    public void stop() {
        broker.close();
    }

    @Override
    public int getPhase() {
        return 1000;
    }
}
