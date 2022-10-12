package io.camunda.zeebe.spring.client.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * @deprecated
 * Use {@link VariablesAsType} instead.
 */
@Deprecated
public @interface ZeebeVariablesAsType {}
