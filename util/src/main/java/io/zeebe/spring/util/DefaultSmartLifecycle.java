package io.zeebe.spring.util;

import org.springframework.context.SmartLifecycle;

public class DefaultSmartLifecycle implements SmartLifecycle
{

    protected boolean isAutoStartup = true;
    protected boolean running = false;

    protected final int phase;
    protected final Runnable onStart;

    public DefaultSmartLifecycle(final int phase, final Runnable onStart)
    {
        this.phase = phase;
        this.onStart = onStart;
    }

    @Override
    public boolean isAutoStartup()
    {
        return isAutoStartup;
    }

    @Override
    public void stop(final Runnable onStop)
    {
        onStop.run();
        running = false;
    }

    @Override
    public void start()
    {
        onStart.run();
        running = true;
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
        return running;
    }

    @Override
    public int getPhase()
    {
        return phase;
    }
}
