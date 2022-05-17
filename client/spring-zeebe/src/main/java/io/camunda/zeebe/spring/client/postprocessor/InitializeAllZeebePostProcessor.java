package io.camunda.zeebe.spring.client.postprocessor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.factory.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

public class InitializeAllZeebePostProcessor implements BeanPostProcessor, Ordered {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final List<AbstractZeebePostProcessor> processors;
  private final ZeebeClientLifecycle clientLifecycle;

  public InitializeAllZeebePostProcessor(List<AbstractZeebePostProcessor> processors, ZeebeClientLifecycle clientLifecycle) {
    this.processors = processors;
    this.clientLifecycle = clientLifecycle;
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
    throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName)
    throws BeansException {
    final ClassInfo beanInfo = ClassInfo.builder().bean(bean).beanName(beanName).build();

    for (final AbstractZeebePostProcessor p : processors) {
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
