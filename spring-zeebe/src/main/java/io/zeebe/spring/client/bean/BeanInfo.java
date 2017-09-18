package io.zeebe.spring.client.bean;

import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import static org.springframework.util.ReflectionUtils.getAllDeclaredMethods;

public interface BeanInfo {

    Object getBean();

    String getBeanName();

    default Class<?> getTargetClass() {
        return AopUtils.getTargetClass(getBean());
    }

    default boolean hasClassAnnotation(final Class<? extends Annotation> type) {
        return getTargetClass().isAnnotationPresent(type);
    }

    default boolean hasMethodAnnotation(final Class<? extends Annotation> type) {
        return Stream.of(getAllDeclaredMethods(getTargetClass())).anyMatch(m -> m.isAnnotationPresent(type));
    }
}
