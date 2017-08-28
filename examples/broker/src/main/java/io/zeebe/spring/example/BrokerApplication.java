package io.zeebe.spring.example;

import io.zeebe.spring.EnableZeebeBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeBroker
public class BrokerApplication {

    public static void main(String... args) {
        SpringApplication.run(BrokerApplication.class, args);
    }
}
