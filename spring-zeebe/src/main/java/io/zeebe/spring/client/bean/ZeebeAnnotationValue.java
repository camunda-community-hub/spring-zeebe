package io.zeebe.spring.client.bean;

public interface ZeebeAnnotationValue<M extends BeanInfo> {

    M getBeanInfo();

}
