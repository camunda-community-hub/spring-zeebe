package io.camunda.zeebe.spring.client.jobhandling.result;

public interface ResultEnricher {

  Object enrich(Object result);
}
