package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.config.SpringZeebeClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class PostProcessorConfiguration {

    @Bean
    public SubscriptionBuilderPostProcessor subscriptionBuilderPostProcessor(final List<BeanInfoPostProcessor> processors, final SpringZeebeClient client) {
        return new SubscriptionBuilderPostProcessor(processors, client);
    }

    @Bean
    public DeploymentPostProcessor deploymentPostProcessor() {
        return new DeploymentPostProcessor();
    }

    @Bean
    public TaskHandlerPostProcessor taskhandlerPostProcessor() {
        return new TaskHandlerPostProcessor();
    }

    @Bean
    public TopicHandlerPostProcessor topicHandlerPostProcessor() {
        return new TopicHandlerPostProcessor();
    }

}
