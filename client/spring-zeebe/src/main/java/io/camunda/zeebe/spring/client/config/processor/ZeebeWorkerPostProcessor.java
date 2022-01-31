package io.camunda.zeebe.spring.client.config.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import java.lang.invoke.MethodHandles;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobHandlerInvokingSpringBeans;
import io.camunda.zeebe.spring.util.ZeebeExpressionResolver;
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

  /**
   * Spring beans the job handler will need to do its job. As the job handler itself will not be a Spring bean,
   * make sure we have them here and pass it on from here.
   */
  private final DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final BackoffSupplier backoffSupplier;

  public ZeebeWorkerPostProcessor(ReadZeebeWorkerValue reader, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy, BackoffSupplier backoffSupplier) {
    this.reader = reader;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.backoffSupplier = backoffSupplier;
  }

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    LOGGER.info("Registering Zeebe worker(s) of bean: {}", beanInfo.getBean());

    final List<ZeebeWorkerValue> annotatedMethods = new ArrayList<>();

    doWithMethods(
      beanInfo.getTargetClass(),
      method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
      ReflectionUtils.USER_DECLARED_METHODS);

    return client ->
      annotatedMethods.forEach(
        zeebeWorkerValue -> {
          final JobWorkerBuilderStep3 builder = client
            .newWorker()
            .jobType(zeebeWorkerValue.getType())
            .handler(new JobHandlerInvokingSpringBeans(zeebeWorkerValue, commandExceptionHandlingStrategy));

          // using defaults from config if null, 0 or negative
          if (zeebeWorkerValue.getName() != null && zeebeWorkerValue.getName().length() > 0) {
            builder.name(zeebeWorkerValue.getName());
          } else {
            builder.name(beanInfo.getBeanName() + "#" + zeebeWorkerValue.getMethodInfo().getMethodName());
          }
          if (zeebeWorkerValue.getMaxJobsActive() > 0) {
            builder.maxJobsActive(zeebeWorkerValue.getMaxJobsActive());
          }
          if (zeebeWorkerValue.getTimeout() > 0) {
            builder.timeout(zeebeWorkerValue.getTimeout());
          }
          if (zeebeWorkerValue.getPollInterval() > 0) {
            builder.pollInterval(Duration.ofMillis(zeebeWorkerValue.getPollInterval()));
          }
          if (zeebeWorkerValue.getRequestTimeout() > 0) {
            builder.requestTimeout(Duration.ofSeconds(zeebeWorkerValue.getRequestTimeout()));
          }
          if (zeebeWorkerValue.getFetchVariables().length > 0) {
            builder.fetchVariables(zeebeWorkerValue.getFetchVariables());
          }

          builder.open();

          LOGGER.info(". Register Zeebe worker: {}", zeebeWorkerValue);
        });
  }


}
