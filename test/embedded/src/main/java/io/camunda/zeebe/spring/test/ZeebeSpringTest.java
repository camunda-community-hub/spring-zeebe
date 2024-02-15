package io.camunda.zeebe.spring.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;

/** Annotation for the Spring test. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
// this pulls in the Configuration NOT as AutoConfiguration but directly creates beans, so the
// marker is present
// when the normal CamundaAutoConfiguration is used by the normal meta-inf/services  way
@Import({CamundaTestAutoConfiguration.class})
// this listener hooks up into test execution
@TestExecutionListeners(
    listeners = ZeebeTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface ZeebeSpringTest {}
