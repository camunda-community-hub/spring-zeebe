package io.zeebe.spring.client;

import io.zeebe.client.TasksClient;
import io.zeebe.client.TopicsClient;
import io.zeebe.client.WorkflowsClient;

public interface ZeebeTemplate {

    /**
     * Provides APIs revolving around task events, such as creating a task.
     */
    TasksClient tasks();

    /**
     * Provides APIs revolving around workflow events, such as creating a workflow instance.
     */
    WorkflowsClient workflows();

    /**
     * Provides cross-cutting APIs related to any topic, such as subscribing to topic events.
     */
    TopicsClient topics();
}
