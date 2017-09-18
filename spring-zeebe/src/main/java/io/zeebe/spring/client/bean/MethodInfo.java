package io.zeebe.spring.client.bean;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

import java.lang.reflect.Method;

@Value
@Builder
public class MethodInfo implements BeanInfo {

    private ClassInfo classInfo;
    private Method method;

    @Override
    public Object getBean() {
        return classInfo.getBean();
    }

    @Override
    public String getBeanName() {
        return classInfo.getBeanName();
    }

    @SneakyThrows
    public Object invoke(final Object... args) {
        return method.invoke(getBean(), args);
    }
}
