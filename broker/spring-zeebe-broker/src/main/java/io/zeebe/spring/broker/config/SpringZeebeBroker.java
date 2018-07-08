package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.SystemContext;
import io.zeebe.spring.util.ZeebeAutoStartUpLifecycle;

public class SpringZeebeBroker extends ZeebeAutoStartUpLifecycle<Broker> {
  private final SystemContext systemContext;

  public SpringZeebeBroker(final SystemContext systemContext) {
    super(1000);
    this.systemContext = systemContext;
  }

  @Override
  public void onStart() {
    delegate = new Broker(systemContext);
  }

  @Override
  public void onStop() {
    delegate.close();
  }

  public SystemContext getBrokerContext() {
    return delegate.getBrokerContext();
  }
}
