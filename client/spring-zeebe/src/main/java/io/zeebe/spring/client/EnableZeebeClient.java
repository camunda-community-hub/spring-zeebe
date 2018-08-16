package io.zeebe.spring.client;

import io.zeebe.spring.client.config.ZeebeClientSpringConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ZeebeClientSpringConfiguration.class)
@Documented
@Inherited
public @interface EnableZeebeClient {

}
