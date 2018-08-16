package io.zeebe.spring.broker;

import io.zeebe.spring.broker.config.ZeebeBrokerConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Annotation that enables the broker config.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ZeebeBrokerConfiguration.class)
@Documented
@Inherited
public @interface EnableZeebeBroker {

}
