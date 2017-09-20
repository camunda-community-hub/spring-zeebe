package io.zeebe.spring.client.bean;

import lombok.Builder;
import lombok.Value;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Value
@Builder
public class ClassInfo implements BeanInfo {

    private Object bean;
    private String beanName;

    public MethodInfo toMethodInfo(final Method method) {
        return MethodInfo.builder()
                .classInfo(this)
                .method(method)
                .build();
    }

    public <T extends Annotation> Optional<T> getAnnotation(final Class<T> type) {
        return Optional.ofNullable(findAnnotation(getTargetClass(), type));
    }
}
