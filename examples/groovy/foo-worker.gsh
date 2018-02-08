#!/usr/bin/env groovy
package io.zeebe.spring.groovy

import groovy.util.logging.Slf4j
import io.zeebe.client.TasksClient
import io.zeebe.client.event.TaskEvent
import io.zeebe.spring.client.EnableZeebeClient
import io.zeebe.spring.client.annotation.ZeebeTaskListener
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@Grab("io.zeebe.spring:spring-zeebe-starter:0.2.0")
@Slf4j
@SpringBootApplication
@EnableZeebeClient
class Application {

    @ZeebeTaskListener(topicName = "default-topic", taskType = "foo", lockOwner = "groovy-worker")
    void workOnTaskFoo(final TasksClient client, final TaskEvent task) {
        log.info("completing task: {}", task)
        client.complete(task)
                .withoutPayload()
                .execute()
    }

}

SpringApplication.run(Application, args)
