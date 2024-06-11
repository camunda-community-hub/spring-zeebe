package io.camunda.zeebe.spring.client.jobhandling.result;

public interface ResultProcessorStrategy {

  ResultProcessor createProcessor(Class<?> resultType);
}
