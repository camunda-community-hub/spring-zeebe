package io.camunda.zeebe.spring.client.config;

import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ZeebeSpringAssertionsExtension implements BeforeEachCallback, AfterEachCallback, TestWatcher {

  @Override
  public void beforeEach(final ExtensionContext extensionContext) throws Exception {
    InMemoryEngine zeebeEngine = SpringExtension.getApplicationContext(extensionContext).getBean(InMemoryEngine.class);
    RecordStreamSourceStore.init(zeebeEngine.getRecordStream());
  }

  @Override
  public void testFailed(final ExtensionContext extensionContext, Throwable cause)  {
    InMemoryEngine zeebeEngine = SpringExtension.getApplicationContext(extensionContext).getBean(InMemoryEngine.class);

    /*
    System.out.print("===== Test failed! Printing records from the log stream =====");
    zeebeEngine.getRecordStream().records().forEach( record -> {System.out.println(record);});
    System.out.print("----------");
    */
  }

  @Override
  public void afterEach(final ExtensionContext extensionContext) {
    // Do we need to close it?
    // ZeebeClient zeebeClient = SpringExtension.getApplicationContext(extensionContext).getBean(ZeebeClient.class);
    // zeebeClient.close();

    RecordStreamSourceStore.reset();
  }
}
