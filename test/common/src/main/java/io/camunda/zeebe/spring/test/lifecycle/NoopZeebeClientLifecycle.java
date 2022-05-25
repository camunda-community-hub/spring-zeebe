package io.camunda.zeebe.spring.test.lifecycle;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import io.camunda.zeebe.spring.client.event.ClientStartedEvent;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import org.springframework.context.ApplicationEventPublisher;

public class NoopZeebeClientLifecycle extends ZeebeClientLifecycle {

  public NoopZeebeClientLifecycle() {
    super(null, null, null);
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

  @Override
  public ZeebeClient get() {
    throw new IllegalStateException("ZeebeClient is not yet created!");
  }
}
