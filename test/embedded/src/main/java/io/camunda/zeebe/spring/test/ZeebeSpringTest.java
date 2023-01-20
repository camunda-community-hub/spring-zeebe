package io.camunda.zeebe.spring.test;

import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
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
@Import({EmbeddedZeebeEngineConfiguration.class ,ZeebeTestClientSpringConfiguration.class})
// this listener hooks up into test execution
@TestExecutionListeners(listeners = ZeebeTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
// I have a strange things without this annotation (i.e. NPE because there is no Zeebe Client in proxy)
@DirtiesContext
public @interface ZeebeSpringTest {

}
