package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.BeanInfo;

public interface ZeebeAnnotationValue<B extends BeanInfo> {

    B getBeanInfo();

}
