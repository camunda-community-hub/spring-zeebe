package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BeanInfoPostProcessor implements BeanNameAware, Predicate<ClassInfo>,
        Function<ClassInfo, Consumer<SpringZeebeClient>> {

    @Getter
    @Setter
    private String beanName;


}
