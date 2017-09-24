package io.zeebe.spring.client.config;

import io.zeebe.client.TasksClient;
import io.zeebe.client.TopicsClient;
import io.zeebe.client.WorkflowsClient;
import io.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import io.zeebe.spring.client.config.resolver.ZeebeExpressionResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(PostProcessorConfiguration.class)
public class ZeebeClientConfiguration {

    @Bean
    public ZeebeExpressionResolver zeebeExpressionResolver() {
        return new ZeebeExpressionResolver();
    }

    @Bean
    public ZeebeClientProperties properties() {
        return ZeebeClientProperties.DEFAULT;
    }

    @Bean
    public SpringZeebeClient springZeebeClient(final ZeebeClientProperties properties, final ApplicationEventPublisher publisher) {
        return new SpringZeebeClient(properties, publisher);
    }

    @Bean
    public WorkflowsClient workflowsClient(final SpringZeebeClient client) {
        return client.workflows();
    }

    @Bean
    public TasksClient tasksClient(final SpringZeebeClient client) {
        return client.tasks();
    }

    @Bean
    public TopicsClient topicsClient(final SpringZeebeClient client) {
        return client.topics();
    }

}
