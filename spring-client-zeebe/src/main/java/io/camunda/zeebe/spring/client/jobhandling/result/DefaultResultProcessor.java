package io.camunda.zeebe.spring.client.jobhandling.result;

public class DefaultResultProcessor implements ResultProcessor {
  @Override
  public Object process(Object result) {
    return result;
  }
}
