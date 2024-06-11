package io.camunda.zeebe.spring.client.jobhandling.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultResultProcessorStrategyTest {

  private final DefaultResultProcessorStrategy resultProcessorStrategy =
      new DefaultResultProcessorStrategy();

  @Test
  void createProcessorShouldReturnDefaultProcessor() {
    // Given
    String inputValue = "input";
    // When
    ResultProcessor resultProcessor =
        resultProcessorStrategy.createProcessor(inputValue.getClass());
    // Then
    Assertions.assertTrue(resultProcessor instanceof DefaultResultProcessor);
  }
}
