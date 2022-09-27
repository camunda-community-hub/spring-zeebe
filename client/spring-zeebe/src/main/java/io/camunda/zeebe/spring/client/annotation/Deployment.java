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
public @interface Deployment {

  // Are there other @Deployment annotations in the JAva Spring Boot space so this could create confusion?
  // Alternative naming ideas:
  // @ZeebeDeployment
  // @CamundaDeployment

  String[] resources() default {};

}
