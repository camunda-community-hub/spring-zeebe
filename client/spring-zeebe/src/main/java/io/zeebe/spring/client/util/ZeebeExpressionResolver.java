package io.zeebe.spring.client.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.expression.StandardBeanExpressionResolver;

import java.util.function.UnaryOperator;

public class ZeebeExpressionResolver implements BeanFactoryAware {

    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();
    private BeanFactory beanFactory;
    private BeanExpressionContext expressionContext;

    /**
     * Resolve the specified value if possible.
     *
     * @see ConfigurableBeanFactory#resolveEmbeddedValue
     */
    private final UnaryOperator<String> resolve = value -> {
        if (this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
        }
        return value;
    };

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory, null);
        }
    }

    public <T> T resolve(String value) {
        final String resolvedValue = resolve.apply(value);

        if (!(resolvedValue.startsWith("#{") && value.endsWith("}"))) {
            return (T) resolvedValue;
        }

        return (T) this.resolver.evaluate(resolvedValue, this.expressionContext);
    }

}
