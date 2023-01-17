package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.filters.RecordStream;

public class EngineUtils {

  private EngineUtils() {}

  public static void initZeebeEngine(final ZeebeTestEngine zeebeTestEngine) {
    BpmnAssert.initRecordStream(
      RecordStream.of(zeebeTestEngine.getRecordStreamSource()));

    ZeebeTestThreadSupport.setEngineForCurrentThread(zeebeTestEngine);
  }
}
