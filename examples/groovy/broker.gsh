#!/usr/bin/env groovy

package io.zeebe.spring.groovy

import io.zeebe.spring.broker.EnableZeebeBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Grab("io.zeebe.spring:spring-zeebe-broker-starter:0.2.0")
@SpringBootApplication
@EnableZeebeBroker
class Application {
}

SpringApplication.run(Application, args)
