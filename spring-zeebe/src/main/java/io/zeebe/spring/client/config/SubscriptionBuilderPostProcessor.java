package io.zeebe.spring.client.config;

import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.config.processor.BeanInfoPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SubscriptionBuilderPostProcessor implements BeanPostProcessor, Ordered {

    private final List<BeanInfoPostProcessor> processors;

    private final SpringZeebeClient client;

    public SubscriptionBuilderPostProcessor(final List<BeanInfoPostProcessor> processors, final SpringZeebeClient client) {
        this.processors = processors;
        this.client = client;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        final ClassInfo beanInfo = ClassInfo.builder()
                .bean(bean)
                .beanName(beanName)
                .build();

        processors.stream()
                .filter(p -> p.test(beanInfo))
                .peek(p -> log.info("processing: {}", beanInfo))
                .map(p -> p.apply(beanInfo))
                .forEach(client::onStart);

        return bean;
    }


    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
