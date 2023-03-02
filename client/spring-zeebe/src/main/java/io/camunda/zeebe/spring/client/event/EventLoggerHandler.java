package io.camunda.zeebe.spring.client.event;

import org.springframework.context.event.EventListener;

@Deprecated
public class EventLoggerHandler {

  @EventListener
  public void logEvent() {
  }
}
