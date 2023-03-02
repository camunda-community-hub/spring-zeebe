package io.camunda.zeebe.spring.client.jobhandling;


import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
  classes = {
    ZeebeClientDisabledInZeebeSpringTest.class
  },
  properties = { "zeebe.client.enabled=false" }
)
@ZeebeSpringTest
public class ZeebeClientDisabledInZeebeSpringTest {

  @Autowired
  private ApplicationContext ctx;

  @Test
  public void testStartup() {
    // a testcase with @ZeebeSpringTests ALWAYS creates a ZeebeEngine and a ZeebeClient (!), even when "zeebe.client.enabled=false" is configured
    // In essence, this is an invalid configuration state - when you don't want to use ZeebeClient, don't use @ZeebeSpringTest
    assertEquals(1, ctx.getBeanNamesForType(ZeebeClient.class).length);
    assertEquals(1, ctx.getBeanNamesForType(ZeebeTestEngine.class).length);
  }
}
