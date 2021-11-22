package io.camunda.zeebe.spring.client.config.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import java.lang.invoke.MethodHandles;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.camunda.zeebe.spring.client.exception.DefaultCommandExceptionHandlingStrategy;
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
  private final DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;

  public ZeebeWorkerPostProcessor(ReadZeebeWorkerValue reader,  DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy) {
    this.reader = reader;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
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
            .handler(new ZeebeWorkerSpringJobHandler(m, commandExceptionHandlingStrategy));

          // using defaults from config if null, 0 or negative
          if (m.getName() != null && m.getName().length() > 0) {
            builder.name(m.getName());
          }
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
