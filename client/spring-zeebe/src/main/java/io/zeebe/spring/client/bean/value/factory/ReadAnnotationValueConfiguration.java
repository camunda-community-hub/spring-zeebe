package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.util.ZeebeExpressionResolver;
import org.springframework.context.annotation.Bean;

/**
 * Bean-Definitions for annotation attribute processing.
 */
public class ReadAnnotationValueConfiguration {

    @Bean
    public ZeebeExpressionResolver zeebeExpressionResolver() {
        return new ZeebeExpressionResolver();
    }

    @Bean
    public ReadZeebeDeploymentValue readZeebeDeploymentValue(final ZeebeExpressionResolver resolver) {
        return new ReadZeebeDeploymentValue(resolver);
    }

    @Bean
    public ReadZeebeTaskListenerValue readZeebeTaskListenerValue(final ZeebeExpressionResolver resolver) {
        return new ReadZeebeTaskListenerValue(resolver);
    }

    @Bean
    public ReadZeebeTopicListenerValue readZeebeTopicListenerValue(final ZeebeExpressionResolver resolver) {
        return new ReadZeebeTopicListenerValue(resolver);
    }
}
