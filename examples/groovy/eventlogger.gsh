#!/usr/bin/env groovy

package io.zeebe.spring.groovy

@Grab("io.zeebe.spring:spring-zeebe-starter:0.3.0-SNAPSHOT")

import groovy.util.logging.Slf4j
import io.zeebe.spring.client.EnableZeebeClient
import io.zeebe.client.event.*
import io.zeebe.spring.client.annotation.ZeebeTopicListener
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@Slf4j
@SpringBootApplication
@EnableZeebeClient
class Application {

    @ZeebeTopicListener(name = "groovy-event-logger", topic = "default-topic")
    void logEvents(GeneralEvent event) {
        final EventMetadata metadata = event.getMetadata();

        log.info(String.format(">>> [topic: %d, position: %d, key: %d, type: %s]\n%s\n===",
                metadata.getPartitionId(),
                metadata.getPosition(),
                metadata.getKey(),
                metadata.getType(),
                event.getJson()))
    }
}

SpringApplication.run(Application, args)