package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.ConfigurationManager;
import org.springframework.context.SmartLifecycle;

import java.util.function.Supplier;

public class SpringZeebeBroker implements SmartLifecycle, Supplier<Broker>
{

    public static final int PHASE = 1000;

    private final ConfigurationManager configurationManager;

    /**
     * Late init during {@link #start()}.
     */
    private Broker broker;

    public SpringZeebeBroker(final ConfigurationManager configurationManager)
    {
        this.configurationManager = configurationManager;
    }

    @Override
    public void start()
    {
        broker = new Broker(configurationManager);
    }

    @Override
    public boolean isRunning()
    {
        return broker != null;
    }

    @Override
    public boolean isAutoStartup()
    {
        return true;
    }

    @Override
    public void stop(final Runnable callback)
    {
        broker.close();
        callback.run();
    }

    @Override
    public void stop()
    {
        this.stop(() -> {
        });
    }

    @Override
    public int getPhase()
    {
        return PHASE;
    }

    @Override
    public Broker get()
    {
        if (!isRunning())
        {
            throw new IllegalStateException("broker is not running!");
        }

        return broker;
    }
}
