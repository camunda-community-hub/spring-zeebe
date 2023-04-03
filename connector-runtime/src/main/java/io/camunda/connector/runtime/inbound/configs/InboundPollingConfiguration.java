package io.camunda.connector.runtime.inbound.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/*
* This is a marker bean to indicate of polling is enabled.
* */
@Component
@ConditionalOnProperty("camunda.connector.polling.enabled")
public class InboundPollingConfiguration {}
