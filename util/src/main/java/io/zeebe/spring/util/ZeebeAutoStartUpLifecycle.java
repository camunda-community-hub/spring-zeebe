package io.zeebe.spring.util;

import java.util.function.Supplier;
import org.springframework.context.SmartLifecycle;

public abstract class ZeebeAutoStartUpLifecycle<T> implements SmartLifecycle, Supplier<T> {

  protected boolean autoStartup = true;
  protected boolean running = false;

  protected final int phase;

  protected T delegate;

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
  public void stop(final Runnable callback) {
    try {
      onStop();
      callback.run();
    } finally {
      running = false;
    }
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
  public void stop() {
    this.stop(() -> {});
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
