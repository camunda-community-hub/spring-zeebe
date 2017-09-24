package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.bean.ZeebeTopicListenerValue;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.util.ReflectionUtils.doWithMethods;

@Slf4j
public class TopicHandlerPostProcessor extends BeanInfoPostProcessor<MethodInfo, ZeebeTopicListener, ZeebeTopicListenerValue> {


    @Override
    public Consumer<SpringZeebeClient> apply(ClassInfo beanInfo) {
        log.info("topic handling: {}", beanInfo);

        final List<ZeebeTopicListenerValue> annotatedMethods = new ArrayList<>();

        doWithMethods(
                beanInfo.getTargetClass(),
                method -> create(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
                ReflectionUtils.USER_DECLARED_METHODS
        );

        return client -> annotatedMethods.forEach(m -> {
            client.topics().newSubscription(m.getTopic())
                    .startAtHeadOfTopic()
                    .forcedStart()
                    .name(m.getName())
                    .handler(event -> m.getBeanInfo().invoke(event))
                    .open();

            log.info("register topicHandler: {}", m);
        });
    }

    @Override
    public boolean test(ClassInfo beanInfo) {
        return beanInfo.hasMethodAnnotation(ZeebeTopicListener.class);
    }

    @Override
    public Class<ZeebeTopicListener> annotationType() {
        return ZeebeTopicListener.class;
    }

    @Override
    public Optional<ZeebeTopicListenerValue> create(final MethodInfo beanInfo) {
        return beanInfo.getAnnotation(annotationType()).map(annotation -> ZeebeTopicListenerValue.builder()
                .beanInfo(beanInfo)
                .name(resolver.resolve(annotation.name()))
                .topic(resolver.resolve(annotation.topic()))
                .build()
        );

    }
}
