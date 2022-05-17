package io.camunda.zeebe.spring.client.factory;

import java.util.function.Supplier;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.context.SmartLifecycle;

/**
 * Bean controlling the lifecycle of a ZebeeClient.
 * Auto creates a ZeebeClient using the {@link ZeebeClientLifecycle} provided by ??
 */
public abstract class AbstractZeebeClientLifecycle implements SmartLifecycle, Supplier<ZeebeClient> {

  protected boolean autoStartup = true;
  protected boolean running = false;
  protected final int phase;

  protected final ZeebeClientObjectFactory factory;
  protected ZeebeClient delegate;

  public AbstractZeebeClientLifecycle(final int phase, final ZeebeClientObjectFactory factory) {
    this.phase = phase;
    this.factory = factory;
  }

  @Override
  public ZeebeClient get() {
    if (!isRunning()) {
      throw new IllegalStateException("ZeebeClient is not yet created!");
    }
    return delegate;
  }

  @Override
  public boolean isAutoStartup() {
    return autoStartup;
  }

  @Override
  public void start() {
    try {
      delegate = factory.getObject();
    } finally {
      running = true;
    }
  }

  @Override
  public void stop(final Runnable callback) {
    try {
      delegate.close();
      callback.run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      running = false;
    }
  }

  @Override
  public void stop() {
    this.stop(() -> {
    });
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public int getPhase() {
    return phase;
  }
}
