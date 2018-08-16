package io.zeebe.spring.client.config.processor;

import io.zeebe.client.ZeebeClient;
import io.zeebe.spring.client.ZeebeClientLifecycle;
import io.zeebe.spring.client.bean.ClassInfo;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionBuilderPostProcessor implements BeanPostProcessor, Ordered {

  private final List<BeanInfoPostProcessor> processors;
  private final ZeebeClientLifecycle clientLifecycle;


  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
    throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName)
    throws BeansException {
    final ClassInfo beanInfo = ClassInfo.builder().bean(bean).beanName(beanName).build();

    for (final BeanInfoPostProcessor p : processors) {
      if (!p.test(beanInfo)) {
        continue;
      }

      final Consumer<ZeebeClient> c = p.apply(beanInfo);
      clientLifecycle.addStartListener(c);
    }

    return bean;
  }

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
