package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import org.springframework.context.SmartLifecycle;

import java.util.function.Supplier;

public class SpringZeebeBroker implements SmartLifecycle, Supplier<Broker> {

    public static final int PHASE = 1000;

    /**
     * Late init during {@link #start()}.
     */
    private Broker broker;

    @Override
    public void start() {
        // FIXME: initialize with toml file path! currently only defaults are used!
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

    @Override
    public Broker get() {
        if (!isRunning()) {
            throw new IllegalStateException("broker is not running!");
        }

        return broker;
    }
}
