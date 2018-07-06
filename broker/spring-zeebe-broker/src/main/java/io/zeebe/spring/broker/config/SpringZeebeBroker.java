package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.SystemContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class SpringZeebeBroker implements SmartLifecycle, Supplier<Broker>
{

    public static final int PHASE = 1000;

    private final SystemContext systemContext;

    /**
     * Late init during {@link #start()}.
     */
    private Broker broker;

    @Override
    public void start()
    {
        broker = new Broker(systemContext);
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
