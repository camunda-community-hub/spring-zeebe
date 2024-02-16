package io.camunda.zeebe.spring.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * @deprecated Use {@link JobWorker} instead. Note, that the default for auto completion has changed
 *     there from "false" to "true"!
 */
@Deprecated
public @interface ZeebeWorker {

  String type() default ""; // set to empty string which leads to method name being used (if not

  // ${zeebe.client.worker.default-type}" is configured) Implemented in
  // ZeebeWorkerAnnotationProcessor

  String name() default
      ""; // set to empty string which leads to default from ZeebeClientBuilderImpl being used in

  // ZeebeWorkerAnnotationProcessor

  long timeout() default -1L;

  int maxJobsActive() default -1;

  long requestTimeout() default -1L;

  long pollInterval() default -1L;

  String[] fetchVariables() default {};

  /**
   * Set to true, all variables are fetched independent of any other configuration via
   * fetchVariables or @ZeebeVariable.
   */
  boolean forceFetchAllVariables() default false;

  /**
   * If set to true, the job is automatically completed after the worker code has finished. In this
   * case, your worker code is not allowed to complete the job itself.
   *
   * <p>You can still throw exceptions if you want to raise a problem instead of job completion. You
   * could also raise a BPMN problem throwing a {@link
   * io.camunda.zeebe.spring.client.exception.ZeebeBpmnError}
   */
  boolean autoComplete() default false;

  boolean enabled() default true;
}
