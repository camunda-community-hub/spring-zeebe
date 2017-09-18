package io.zeebe.spring.client.bean;

import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Method;

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
}
