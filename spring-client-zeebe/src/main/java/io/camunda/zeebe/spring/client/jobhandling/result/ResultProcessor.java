package io.camunda.zeebe.spring.client.jobhandling.result;

public interface ResultProcessor {

  Object process(Object result);
}
