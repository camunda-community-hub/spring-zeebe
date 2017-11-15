package io.zeebe.spring.client.config.processor;

import io.zeebe.client.ZeebeClient;
import io.zeebe.spring.client.bean.ClassInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BeanInfoPostProcessor implements BeanNameAware, Predicate<ClassInfo>,
        Function<ClassInfo, Consumer<ZeebeClient>>
{

    @Getter
    @Setter
    private String beanName;


}
