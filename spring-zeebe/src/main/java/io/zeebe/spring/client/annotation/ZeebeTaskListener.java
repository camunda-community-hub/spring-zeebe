package io.zeebe.spring.client.annotation;

import io.zeebe.client.task.impl.subscription.TaskSubscriptionBuilderImpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeTaskListener {

    String topicName();

    String taskType();

    String lockOwner();

    long lockTime();

    int taskFetchSize() default TaskSubscriptionBuilderImpl.DEFAULT_TASK_FETCH_SIZE;

}
