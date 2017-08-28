package io.zeebe.spring.client;

import io.zeebe.client.TasksClient;
import io.zeebe.client.TopicsClient;
import io.zeebe.client.WorkflowsClient;
import io.zeebe.client.ZeebeClient;
import io.zeebe.spring.client.fn.CreateTaskSubscriptionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

public class ZeebeClientConfiguration {



    @Bean
    public ZeebeClient zeebeClient() {
        return new ZeebeClientLifecycle();
    }



    @Bean
    public WorkflowsClient workflowsClient(ZeebeClient client) {
        return client.workflows();
    }

    @Bean
    public TasksClient tasksClient(ZeebeClient client) {
        return client.tasks();
    }

    @Bean
    public TopicsClient topicsClient(ZeebeClient client) {
        return client.topics();
    }


}
