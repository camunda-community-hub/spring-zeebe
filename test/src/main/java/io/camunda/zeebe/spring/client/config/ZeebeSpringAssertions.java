package io.camunda.zeebe.spring.client.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//@EmbeddedZeebeEngine
@Import(ZeebeTestClientSpringConfiguration.class)
@ExtendWith(ZeebeSpringAssertionsExtension.class)
public @interface ZeebeSpringAssertions {}
