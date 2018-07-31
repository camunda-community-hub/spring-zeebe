package io.zeebe.spring.broker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZeebeBrokerLifecycleITest {

  @SpringBootApplication
  @EnableZeebeBroker
  public static class TestBrokerApplication {

    public static void main(final String[] args) {
      SpringApplication.run(TestBrokerApplication.class, args);
    }
  }

  @Test
  public void canStart() {
    // ensure the broker can be started
  }
}
