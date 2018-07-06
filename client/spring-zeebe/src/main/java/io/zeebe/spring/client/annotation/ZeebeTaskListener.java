package io.zeebe.spring.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeTaskListener
{

    String topic() default "${zeebe.topic}";

    String taskType();

    String lockOwner() default "${zeebe.lockOwner}";

    long lockTime() default 10000L;

    int taskFetchSize() default 1; // FIXME: DEFAULT_TASK_FETCH_SIZE

}
