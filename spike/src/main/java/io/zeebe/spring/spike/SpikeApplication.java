package io.zeebe.spring.spike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpikeApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpikeApplication.class, args);
    }

    @Bean
    ClientConfiguration clientConfiguration() {
        return new ClientConfiguration() {
            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 8080;
            }

            @Override
            public boolean isAutoStartup() {
                return true;
            }
        };
    }
}
