package io.camunda.zeebe.spring.client.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * @deprecated Use {@link Variable} instead.
 */
@Deprecated
public @interface ZeebeVariable {}
