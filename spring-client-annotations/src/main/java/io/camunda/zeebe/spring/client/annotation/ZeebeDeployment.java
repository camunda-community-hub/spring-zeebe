package io.camunda.zeebe.spring.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited // has to be inherited to work on spring aop beans
/**
 * @deprecated Use {@link Deployment} instead.
 */
@Deprecated
public @interface ZeebeDeployment {

  @Deprecated
  String[] classPathResources() default {};

  String[] resources() default {};
}
