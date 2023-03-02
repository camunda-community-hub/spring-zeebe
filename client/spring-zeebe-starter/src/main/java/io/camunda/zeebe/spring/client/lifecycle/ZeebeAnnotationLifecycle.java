package io.camunda.zeebe.spring.client.lifecycle;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.ZeebeClientConfiguration;
import io.camunda.zeebe.spring.client.event.ClientStartedEvent;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import io.camunda.zeebe.spring.client.event.ClientStoppedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;

/**
 * Lifecycle implementation that also directly acts as a ZeebeClient by delegating all methods to the
 * ZeebeClient that is controlled (and kept in the delegate field)
 *
 * Created by {@link ZeebeClientConfiguration} (only for production code, not for tests)
 */
public class ZeebeAnnotationLifecycle implements SmartLifecycle {

  protected boolean running = false;

  private final ZeebeAnnotationProcessorRegistry annotationProcessorRegistry;
  private final ApplicationEventPublisher publisher;

  private final ZeebeClient client;

  public ZeebeAnnotationLifecycle(final ZeebeClient client, final ZeebeAnnotationProcessorRegistry annotationProcessorRegistry, final ApplicationEventPublisher publisher) {
    this.client = client;
    this.annotationProcessorRegistry = annotationProcessorRegistry;
    this.publisher = publisher;
  }

  @Override
  public void start() {
    publisher.publishEvent(new ClientStartedEvent());
    annotationProcessorRegistry.startAll(client);
    this.running = true;
  }

  @Override
  public void stop() {
    publisher.publishEvent(new ClientStoppedEvent());
    annotationProcessorRegistry.stopAll(client);
    this.running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
  }

}
