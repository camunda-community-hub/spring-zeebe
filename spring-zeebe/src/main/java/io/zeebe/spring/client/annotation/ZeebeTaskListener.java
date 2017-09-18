package io.zeebe.spring.client.annotation;

import io.zeebe.client.task.impl.subscription.TaskSubscriptionBuilderImpl;
import io.zeebe.spring.client.bean.MethodInfo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeTaskListener {

    @Value
    @Slf4j
    @Builder
    class Annotated {

        public static Annotated of(final MethodInfo methodInfo, final ZeebeTaskListener annotation) {
            return Annotated.builder().beanInfo(methodInfo)
                    .topicName(annotation.topicName())
                    .taskType(annotation.taskType())
                    .lockOwner(annotation.lockOwner())
                    .lockTime(annotation.lockTime())
                    .taskFetchSize(annotation.taskFetchSize())
                    .build();
        }

        private MethodInfo beanInfo;

        private String topicName;

        private String taskType;

        private String lockOwner;

        private long lockTime;

        private int taskFetchSize;
    }

    String topicName();

    String taskType();

    String lockOwner();

    long lockTime();

    int taskFetchSize() default TaskSubscriptionBuilderImpl.DEFAULT_TASK_FETCH_SIZE;

}
