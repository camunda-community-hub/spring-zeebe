package io.zeebe.spring.api

import io.zeebe.spring.api.command.CreateDeployment
import io.zeebe.test.ZeebeTestRule
import org.junit.Rule
import org.junit.Test

class SpringZeebeApiKtTest {

  @get: Rule
  val zeebe = ZeebeTestRule()

  @Test
  fun `deploy demo process`() {
    zeebe.client.apply(CreateDeployment("demoProcess.bpmn")).join()
  }
}
