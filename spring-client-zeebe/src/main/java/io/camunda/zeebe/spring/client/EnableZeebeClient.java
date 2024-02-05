package io.camunda.zeebe.spring.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Deprecated // This annotation has no effect any more and will be removed in some later version of
// spring-zeebe, see https://github.com/camunda-community-hub/spring-zeebe/issues/275
/**
 * @deprecated This annotation does not have any effect any more from 8.2.x on and can simply be
 *     removed
 */
public @interface EnableZeebeClient {}
