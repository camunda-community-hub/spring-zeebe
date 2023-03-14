package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.spring.client.CamundaAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;
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
// this pulls in the Configuration NOT as AutoConfiguration but directly creates beans, so the marker is present
// when the normal CamundaAutoConfiguration is used by the normal meta-inf/services  way
@Import({CamundaTestAutoConfiguration.class})
// this listener hooks up into test execution
@TestExecutionListeners(listeners = ZeebeTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface ZeebeSpringTest {

}
