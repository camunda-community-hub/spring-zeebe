package io.zeebe.spring.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.WorkflowInstanceResult;
import io.zeebe.containers.ZeebeContainer;
import java.util.HashMap;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {WorkerApplicationTest.Initializer.class})
public class WorkerApplicationTest {
  @ClassRule public static ZeebeContainer zeebeContainer = new ZeebeContainer();

  @Autowired ZeebeClient client;

  @Test
  public void shouldHandleJobs() {
    // given
    final Map<String, Object> variables = new HashMap<>();
    variables.put("a", 0);

    // when
    final WorkflowInstanceResult result =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("demoProcess")
            .latestVersion()
            .variables(variables)
            .withResult()
            .fetchVariables("foo", "bar")
            .send()
            .join();

    // then
    assertThat(result.getVariablesAsMap(), allOf(hasEntry("foo", 1), hasEntry("bar", 1)));
  }

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "zeebe.client.worker.defaultName=foo-worker",
              "zeebe.client.broker.contactPoint=" + zeebeContainer.getExternalGatewayAddress(),
              "zeebe.client.security.plaintext=true")
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
