package io.zeebe.spring.client.bean.value;

import io.zeebe.spring.client.bean.BeanInfo;

/**
 * Common type for all annotation values.
 *
 * @param <B> either {@link io.zeebe.spring.client.bean.ClassInfo} or {@link
 * io.zeebe.spring.client.bean.MethodInfo}.
 */
public interface ZeebeAnnotationValue<B extends BeanInfo> {

  /**
   * @return the context of this annotation.
   */
  B getBeanInfo();
}
