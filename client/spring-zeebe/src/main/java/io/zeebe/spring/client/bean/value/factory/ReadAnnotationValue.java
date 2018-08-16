package io.zeebe.spring.client.bean.value.factory;

import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.value.ZeebeAnnotationValue;
import io.zeebe.spring.util.ZeebeExpressionResolver;
import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

/**
 * Reads the annotation properties of {@link Annotation} from given {@link BeanInfo} and returns
 * them as {@link ZeebeAnnotationValue}.
 *
 * @param <B> type of {@link BeanInfo}, either {@link io.zeebe.spring.client.bean.ClassInfo} or
 * {@link io.zeebe.spring.client.bean.MethodInfo}.
 * @param <A> type of Annotation to read
 * @param <V> pojo representation of the annotation properties and method/class context
 */
abstract class ReadAnnotationValue<
  B extends BeanInfo, A extends Annotation, V extends ZeebeAnnotationValue<B>>
  implements Function<B, Optional<V>> {

  protected final ZeebeExpressionResolver resolver;
  protected final Class<A> annotationType;

  protected ReadAnnotationValue(
    final ZeebeExpressionResolver resolver, final Class<A> annotationType) {
    this.resolver = resolver;
    this.annotationType = annotationType;
  }

  public V applyOrThrow(final B beanInfo) {
    return apply(beanInfo).orElseThrow(BeanInfo.noAnnotationFound(annotationType));
  }
}
