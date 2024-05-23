package io.camunda.zeebe.spring.client.jobhandling.result;

public class DefaultResultEnricher implements ResultEnricher {
  @Override
  public Object enrich(Object result) {
    return result;
  }
}
