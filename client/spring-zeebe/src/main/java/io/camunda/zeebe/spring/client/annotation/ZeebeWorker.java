package io.camunda.zeebe.spring.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeWorker {

  String type() default "${zeebe.client.worker.default-type}";

  String name() default ""; // set to empty string which leads to default from ZeebeClientBuilderImpl being used in ZeebeWorkerPostProcessor

  long timeout() default -1L;

  int maxJobsActive() default -1;

  long requestTimeout() default -1L;

  long pollInterval() default -1L;

  String[] fetchVariables() default {};

  /**
   * Set to true, all variables are fetched independent of any other configuration
   * via fetchVariables or @ZeebeVariable.
   */
  boolean forceFetchAllVariables() default false;

  /**
   * If set to true, the job is automatically completed after the worker code has finished.
   * In this case, your worker code is not allowed to complete the job itself.
   *
   *  You can still throw exceptions if you want to raise a problem instead of job completion.
   *  You could also raise a BPMN problem throwing a {@link io.camunda.zeebe.spring.client.exception.ZeebeBpmnError}
   */
  boolean autoComplete() default false;

  boolean enabled() default true;
}
