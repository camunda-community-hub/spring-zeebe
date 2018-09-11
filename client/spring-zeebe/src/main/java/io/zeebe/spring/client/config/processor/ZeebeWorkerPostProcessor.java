package io.zeebe.spring.client.config.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.subscription.JobWorker;
import io.zeebe.spring.api.SpringZeebeApiKt;
import io.zeebe.spring.api.command.CreateJobWorker;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

/**
 * Triggered by {@link SubscriptionBuilderPostProcessor#postProcessAfterInitialization(Object,
 * String)} to add Handler subscriptions for {@link ZeebeWorker} method-annotations.
 */
@Slf4j
@RequiredArgsConstructor
public class ZeebeWorkerPostProcessor extends BeanInfoPostProcessor {

  private final ReadZeebeWorkerValue reader;

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    log.info("zeebeWorker: {}", beanInfo);

    final List<ZeebeWorkerValue> annotatedMethods = new ArrayList<>();

    doWithMethods(
      beanInfo.getTargetClass(),
      method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
      ReflectionUtils.USER_DECLARED_METHODS);

    return client ->
      annotatedMethods.forEach(
        m -> {
          final JobWorker jobWorker = SpringZeebeApiKt.apply(client,
            new CreateJobWorker(
              m.getTopicName(),
              m.getJobType(),
              (jobClient, job) -> m.getBeanInfo().invoke(jobClient, job))
          );

          log.info("register taskHandler: {}", m);
        });
  }
}
