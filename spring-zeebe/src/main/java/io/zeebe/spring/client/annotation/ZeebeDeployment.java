package io.zeebe.spring.client.annotation;

import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.ClassInfo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited // has to be inherited to work on spring aop beans
public @interface ZeebeDeployment {

    @Slf4j
    @Value
    @Builder
    class Annotated {

        public static Annotated of(final ClassInfo classInfo) {
            return of(
                    classInfo,
                    classInfo.getAnnotation(ZeebeDeployment.class)
                            .orElseThrow(BeanInfo.noAnnotationFound(ZeebeDeployment.class))
            );
        }

        public static Annotated of(final ClassInfo classInfo, final ZeebeDeployment annotation) {
            return Annotated.builder().beanInfo(classInfo)
                    .topicName(annotation.topicName())
                    .classPathResource(annotation.classPathResource())
                    .build();
        }

        String topicName;

        String classPathResource;

        ClassInfo beanInfo;
    }

    String topicName();

    String classPathResource();

}
