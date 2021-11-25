package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.bpmnassert.RecordStreamSourceStore;
import org.camunda.community.eze.ZeebeEngine;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ZeebeSpringAssertionsExtension implements BeforeEachCallback, AfterEachCallback, TestWatcher {

  @Override
  public void beforeEach(final ExtensionContext extensionContext) throws Exception {
    ZeebeEngine zeebeEngine = SpringExtension.getApplicationContext(extensionContext).getBean(ZeebeEngine.class);
    RecordStreamSourceStore.init(zeebeEngine);
  }

  @Override
  public void testFailed(final ExtensionContext extensionContext, Throwable cause)  {
    ZeebeEngine zeebeEngine = SpringExtension.getApplicationContext(extensionContext).getBean(ZeebeEngine.class);

    System.out.print("===== Test failed! Printing records from the log stream =====");
    zeebeEngine.records().forEach( record -> {System.out.println(record);});
    System.out.print("----------");
  }

  @Override
  public void afterEach(final ExtensionContext extensionContext) {
    // Do we need to close it?
    // ZeebeClient zeebeClient = SpringExtension.getApplicationContext(extensionContext).getBean(ZeebeClient.class);
    // zeebeClient.close();

    RecordStreamSourceStore.reset();
  }
}
