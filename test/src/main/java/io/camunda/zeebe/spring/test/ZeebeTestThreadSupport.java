package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.RecordStreamSourceStore;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Objects;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;

/**
 * Helper to wait in the multi-threaded environment for the worker to execute.
 */
public class ZeebeTestThreadSupport {

  private final static ThreadLocal<InMemoryEngine> ENGINES = new ThreadLocal<>();
  private final static Duration DEFAULT_DURATION = Duration.ofMillis(5000);
  private final static Long DEFAULT_INTERVAL_MILLIS = 100L;

  public static void setEngineForCurrentThread(InMemoryEngine engine) {
    ENGINES.set(engine);
  }

  public static void cleanupEngineForCurrentThread() {
    ENGINES.remove();
  }

  public static void waitForProcessInstanceCompleted(ProcessInstanceEvent processInstance) {
    waitForProcessInstanceCompleted(processInstance, DEFAULT_DURATION);
  }

  public static void waitForProcessInstanceCompleted(ProcessInstanceEvent processInstance, Duration duration) {
    // get it in the thread of the test
    final InMemoryEngine engine = ENGINES.get();
    if (engine == null) {
      throw new IllegalStateException("No Zeebe engine is initialized for the current thread, annotate the test with @ZeebeSpringTest");
    }
    if (duration == null) {
      duration = DEFAULT_DURATION;
    }
    Awaitility.await().atMost(duration).untilAsserted(() -> {
      // allow the worker to work
      Thread.sleep(DEFAULT_INTERVAL_MILLIS);
      RecordStreamSourceStore.init(Objects.requireNonNull(engine).getRecordStream());
      // use inside the awaitility thread
      assertThat(processInstance).isCompleted();
    });
  }

  public static void waitForProcessInstanceHasPassedElement(ProcessInstanceEvent processInstance, String elementId) {
    waitForProcessInstanceHasPassedElement(processInstance, elementId, DEFAULT_DURATION);
  }

  public static void waitForProcessInstanceHasPassedElement(ProcessInstanceEvent processInstance, String elementId, Duration duration) {
    final InMemoryEngine engine = ENGINES.get();
    if (engine == null) {
      throw new IllegalStateException("No Zeebe engine is initialized for the current thread, annotate the test with @ZeebeSpringTest");
    }
    if (duration == null) {
      duration = DEFAULT_DURATION;
    }
    Awaitility.await().atMost(duration).untilAsserted(() -> {
      Thread.sleep(DEFAULT_INTERVAL_MILLIS);
      RecordStreamSourceStore.init(Objects.requireNonNull(engine).getRecordStream());
      assertThat(processInstance).hasPassedElement(elementId);
    });
  }
}
