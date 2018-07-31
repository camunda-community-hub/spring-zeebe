package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.SystemContext;
import io.zeebe.spring.util.ZeebeAutoStartUpLifecycle;

/**
 * Wraps the {@link Broker} in a {@link org.springframework.context.SmartLifecycle} to start/stop
 * within the scope of a spring boot application.
 */
public class ZeebeBrokerLifecycle extends ZeebeAutoStartUpLifecycle<Broker> {

  public static final int PHASE = 1000;

  private final ZeebeBrokerFactory brokerFactory;

  public ZeebeBrokerLifecycle(final ZeebeBrokerFactory brokerFactory) {
    super(PHASE);

    this.brokerFactory = brokerFactory;
  }

  @Override
  public void onStart() {
    delegate = brokerFactory.create();
  }

  @Override
  public void onStop() {
    delegate.close();
  }

  public SystemContext getBrokerContext() {
    return delegate.getBrokerContext();
  }
}
