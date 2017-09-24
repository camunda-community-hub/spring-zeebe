package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.ZeebeAnnotationValue;
import io.zeebe.spring.client.config.SpringZeebeClient;
import io.zeebe.spring.client.config.resolver.ZeebeExpressionResolver;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BeanInfoPostProcessor<M extends BeanInfo, A extends Annotation, T extends ZeebeAnnotationValue<M>> implements BeanNameAware,
        Function<ClassInfo, Consumer<SpringZeebeClient>>,
        Predicate<ClassInfo> {

    @Getter
    @Setter
    private String beanName;

    @Autowired
    protected ZeebeExpressionResolver resolver;

    public abstract Class<A> annotationType();

    public abstract Optional<T> create(M beanInfo);


}
