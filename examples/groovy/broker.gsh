#!/usr/bin/env groovy

package io.zeebe.spring.groovy

@Grab("io.zeebe.spring:spring-zeebe-broker-starter:0.3.0-SNAPSHOT")

import io.zeebe.spring.broker.EnableZeebeBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeBroker
class Application {
}

SpringApplication.run(Application, args)
