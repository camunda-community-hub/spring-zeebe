package io.zeebe.spring.client.config.processor;

import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeTopicListenerValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeTopicListenerValue;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.util.ReflectionUtils.doWithMethods;

@Slf4j
public class TopicHandlerPostProcessor extends BeanInfoPostProcessor {


    private final ReadZeebeTopicListenerValue reader;

    public TopicHandlerPostProcessor(final ReadZeebeTopicListenerValue reader) {
        this.reader = reader;
    }

    @Override
    public Consumer<SpringZeebeClient> apply(ClassInfo beanInfo) {
        log.info("topic handling: {}", beanInfo);

        final List<ZeebeTopicListenerValue> annotatedMethods = new ArrayList<>();

        doWithMethods(
                beanInfo.getTargetClass(),
                method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
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
