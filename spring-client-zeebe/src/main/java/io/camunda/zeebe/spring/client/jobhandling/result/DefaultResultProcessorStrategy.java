package io.camunda.zeebe.spring.client.jobhandling.result;

public class DefaultResultProcessorStrategy implements ResultProcessorStrategy {
  @Override
  public ResultProcessor createProcessor(Class<?> resultType) {
    return new DefaultResultProcessor();
  }
}
