package io.zeebe.spring.broker;

import io.zeebe.spring.broker.test.BrokerTestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BrokerTestApplication.class)
public class ZeebeBrokerLifecycleITest {


  @Test
  public void canStart() {
    // ensure the broker can be started
  }
}
