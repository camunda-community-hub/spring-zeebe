package io.zeebe.spring.broker;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.SystemContext;
import io.zeebe.spring.util.ZeebeAutoStartUpLifecycle;

/**
 * Wraps the {@link Broker} in a {@link org.springframework.context.SmartLifecycle} to start/stop
 * within the scope of a spring boot application.
 */
public class ZeebeBrokerLifecycle extends ZeebeAutoStartUpLifecycle<Broker> {

  public static final int PHASE = 1000;

  public ZeebeBrokerLifecycle(final ZeebeBrokerObjectFactory factory) {
    super(PHASE, factory);
  }

  public SystemContext getBrokerContext() {
    return get().getBrokerContext();
  }
}
