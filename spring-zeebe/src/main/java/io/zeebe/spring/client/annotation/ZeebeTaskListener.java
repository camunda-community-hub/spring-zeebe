package io.zeebe.spring.client.annotation;

import io.zeebe.client.task.impl.subscription.TaskSubscriptionBuilderImpl;
import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.MethodInfo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.zeebe.spring.client.bean.BeanInfo.noAnnotationFound;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeTaskListener {

    String topic() default "${zeebe.topic}";

    String taskType();

    String lockOwner()  default "${zeebe.lockOwner}";

    long lockTime() default 10000L;

    int taskFetchSize() default TaskSubscriptionBuilderImpl.DEFAULT_TASK_FETCH_SIZE;

}
