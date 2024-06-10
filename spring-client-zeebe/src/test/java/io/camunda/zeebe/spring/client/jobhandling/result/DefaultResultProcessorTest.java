package io.camunda.zeebe.spring.client.jobhandling.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultResultProcessorTest {

  private final DefaultResultProcessor defaultResultProcessor = new DefaultResultProcessor();

  @Test
  public void testProcessMethodShouldReturnResult() {
    //Given
    String inputValue = "input";
    //When
    Object resultValue = defaultResultProcessor.process(inputValue);
    //Then
    Assertions.assertEquals(inputValue, resultValue);
  }

}
