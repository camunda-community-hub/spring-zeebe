package io.zeebe.spring.client.config.processor;

import io.zeebe.client.event.EventMetadata;
import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.MethodInfo;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.doWithMethods;

@Slf4j
public class TopicHandlerPostProcessor extends BeanInfoPostProcessor {


    @Override
    public Consumer<SpringZeebeClient> apply(ClassInfo beanInfo) {
        log.info("topic handling: {}", beanInfo);

        final List<ZeebeTopicListener.Annotated> annotatedMethods = new ArrayList<>();

        doWithMethods(
                beanInfo.getTargetClass(),
                method -> {
                    MethodInfo m = beanInfo.toMethodInfo(method);
                    Optional.ofNullable(findAnnotation(method, ZeebeTopicListener.class))
                            .ifPresent(a -> annotatedMethods.add(ZeebeTopicListener.Annotated.of(beanInfo.toMethodInfo(method), a)));
                },
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
}
