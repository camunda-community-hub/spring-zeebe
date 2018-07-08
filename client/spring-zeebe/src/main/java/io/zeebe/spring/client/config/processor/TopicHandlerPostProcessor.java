package io.zeebe.spring.client.config.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import io.zeebe.client.ZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeTopicListener;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeTopicListenerValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeTopicListenerValue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class TopicHandlerPostProcessor extends BeanInfoPostProcessor {

  private final ReadZeebeTopicListenerValue reader;

  public TopicHandlerPostProcessor(final ReadZeebeTopicListenerValue reader) {
    this.reader = reader;
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    log.info("topic handling: {}", beanInfo);

    final List<ZeebeTopicListenerValue> annotatedMethods = new ArrayList<>();

    doWithMethods(
        beanInfo.getTargetClass(),
        method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
        ReflectionUtils.USER_DECLARED_METHODS);

    return client ->
        annotatedMethods.forEach(
            m -> {
              client
                  .topicClient(m.getTopic())
                  .newSubscription()
                  .name(m.getName())
                  .jobEventHandler(event -> m.getBeanInfo().invoke(event))
                  .startAtHeadOfTopic()
                  .forcedStart()
                  .open();

              log.info("register topicHandler: {}", m);
            });
  }

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(ZeebeTopicListener.class);
  }
}
