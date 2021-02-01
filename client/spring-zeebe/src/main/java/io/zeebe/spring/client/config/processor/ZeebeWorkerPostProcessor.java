package io.zeebe.spring.client.config.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import java.lang.invoke.MethodHandles;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * Triggered by {@link SubscriptionBuilderPostProcessor#postProcessAfterInitialization(Object, String)} to add Handler subscriptions for {@link ZeebeWorker}
 * method-annotations.
 */
public class ZeebeWorkerPostProcessor extends BeanInfoPostProcessor {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ReadZeebeWorkerValue reader;

  public ZeebeWorkerPostProcessor(ReadZeebeWorkerValue reader) {
    this.reader = reader;
  }

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    LOGGER.info("zeebeWorker: {}", beanInfo);

    final List<ZeebeWorkerValue> annotatedMethods = new ArrayList<>();

    doWithMethods(
      beanInfo.getTargetClass(),
      method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
      ReflectionUtils.USER_DECLARED_METHODS);

    return client ->
      annotatedMethods.forEach(
        m -> {
          final JobWorkerBuilderStep3 builder = client
            .newWorker()
            .jobType(m.getType())
            .handler((jobClient, job) -> m.getBeanInfo().invoke(jobClient, job))
            .name(m.getName());

          // using defaults from config if negative
          if (m.getMaxJobsActive() > 0) {
            builder.maxJobsActive(m.getMaxJobsActive());
          }
          if (m.getTimeout() > 0) {
            builder.timeout(m.getTimeout());
          }
          if (m.getPollInterval() > 0) {
            builder.pollInterval(Duration.ofMillis(m.getPollInterval()));
          }
          if (m.getRequestTimeout() > 0) {
            builder.requestTimeout(Duration.ofSeconds(m.getRequestTimeout()));
          }
          if (m.getFetchVariables().length > 0) {
            builder.fetchVariables(m.getFetchVariables());
          }

          builder.open();

          LOGGER.info("register job worker: {}", m);
        });
  }
}
