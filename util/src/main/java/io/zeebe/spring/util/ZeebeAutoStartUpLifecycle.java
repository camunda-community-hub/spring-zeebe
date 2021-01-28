package io.zeebe.spring.util;

import java.util.function.Supplier;
import org.springframework.context.SmartLifecycle;

/**
 * Implementation of {@link SmartLifecycle} that delegates to a delegate of type <code>T</code> and
 * defaults to <code>autostart</code>.
 *
 * @param <T> type of delegate to start/stop
 */
public abstract class ZeebeAutoStartUpLifecycle<T extends AutoCloseable> implements SmartLifecycle,
  Supplier<T> {

  protected boolean autoStartup = true;
  protected boolean running = false;

  protected final int phase;
  protected final ZeebeObjectFactory<T> factory;

  protected T delegate;

  /**
   * Creates a new lifecycle.
   *
   * @param phase the phase to run in
   */
  public ZeebeAutoStartUpLifecycle(final int phase,
                                   final ZeebeObjectFactory<T> factory) {
    this.phase = phase;
    this.factory = factory;
  }

  @Override
  public T get() {
    if (!isRunning()) {
      throw new IllegalStateException("delegate is not running!");
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
