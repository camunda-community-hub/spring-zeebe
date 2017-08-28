package io.zeebe.spring;

import io.zeebe.client.event.TopicSubscription;
import io.zeebe.client.task.PollableTaskSubscription;
import io.zeebe.client.task.TaskHandler;
import io.zeebe.client.task.TaskSubscription;
import io.zeebe.client.task.TaskSubscriptionBuilder;
import io.zeebe.client.task.impl.subscription.TaskSubscriptionBuilderImpl;
import io.zeebe.spring.client.ZeebeClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZeebeTaskSubscription {


    String topicName();

    String taskType();

    String lockOwner();

    long lockTime();

    int taskFetchSize() default TaskSubscriptionBuilderImpl.DEFAULT_TASK_FETCH_SIZE;

}
