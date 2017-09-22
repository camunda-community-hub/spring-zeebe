package io.zeebe.spring.client.annotation;

import io.zeebe.spring.client.bean.MethodInfo;
import lombok.Builder;
import lombok.Value;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.zeebe.spring.client.bean.BeanInfo.noAnnotationFound;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeTopicListener {

    @Value
    @Builder
    class Annotated {

        public static Annotated of(final MethodInfo methodInfo) {
            return of(
                    methodInfo,
                    methodInfo.getAnnotation(ZeebeTopicListener.class)
                            .orElseThrow(noAnnotationFound(ZeebeTopicListener.class))
            );
        }

        public static Annotated of(final MethodInfo methodInfo, ZeebeTopicListener annotation) {
            return Annotated.builder()
                    .beanInfo(methodInfo)
                    .name(annotation.name())
                    .topic(annotation.topic())
                    .build();
        }

        private String name;

        private String topic;

        private MethodInfo beanInfo;

    }

    String name();

    String topic();
}
