package io.zeebe.spring.util;

import java.util.function.Supplier;
import org.springframework.context.SmartLifecycle;

/**
 * Implementation of {@link SmartLifecycle} that delegates to a delegate of type <code>T</code> and
 * defaults to <code>autostart</code>.
 *
 * Overwrite the {@link #onStart()} and {@link #onStop()} methods and define a phase in constructor
 * to start/stop any delegate service.
 *
 * @param <T> type of delegate to start/stop
 */
public abstract class ZeebeAutoStartUpLifecycle<T> implements SmartLifecycle, Supplier<T> {

  protected boolean autoStartup = true;
  protected boolean running = false;

  protected final int phase;

  protected T delegate;

  /**
   * Creates a new lifecycle.
   *
   * @param phase the phase to run in
   */
  public ZeebeAutoStartUpLifecycle(final int phase) {
    this.phase = phase;
  }

  public abstract void onStart();

  public abstract void onStop();

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
      onStart();
    } finally {
      running = true;
    }
  }

  @Override
  public void stop(final Runnable callback) {
    try {
      onStop();
      callback.run();
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
