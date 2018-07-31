package io.zeebe.spring.broker.test;

import io.zeebe.spring.broker.EnableZeebeBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A spring boot application with component scan in <code>io.zeebe.spring.broker.test</code>.
 */
@SpringBootApplication
@EnableZeebeBroker
public class BrokerTestApplication {

  public static void main(final String... args) {
    SpringApplication.run(BrokerTestApplication.class, args);
  }
}
