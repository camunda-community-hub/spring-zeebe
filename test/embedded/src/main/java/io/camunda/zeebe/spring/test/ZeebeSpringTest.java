package io.camunda.zeebe.spring.test;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for the Spring test.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
// this creates the engine and the client
@ImportAutoConfiguration({ZeebeTestConfiguration.class})
// this listener hooks up into test execution
@TestExecutionListeners(listeners = ZeebeTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface ZeebeSpringTest {

}
