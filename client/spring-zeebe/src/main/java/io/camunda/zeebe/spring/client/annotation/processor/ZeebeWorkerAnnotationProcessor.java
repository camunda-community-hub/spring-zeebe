package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadZeebeWorkerValue;
import io.camunda.zeebe.spring.client.jobhandling.DefaultCommandExceptionHandlingStrategy;
import io.camunda.zeebe.spring.client.jobhandling.JobHandlerInvokingSpringBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ReflectionUtils.doWithMethods;

/**
 * Always created by {@link AnnotationProcessorConfiguration}
 *
 * Triggered by {@link ZeebeAnnotationProcessorRegistry#postProcessAfterInitialization(Object, String)} to add Handler subscriptions for {@link ZeebeWorker}
 * method-annotations.
 */
public class ZeebeWorkerAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ReadZeebeWorkerValue reader;

  /**
   * Spring beans the job handler will need to do its job. As the job handler itself will not be a Spring bean,
   * make sure we have them here and pass it on from here.
   */
  private final DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy;
  private final BackoffSupplier backoffSupplier;

  private String beanName = null;
  private List<ZeebeWorkerValue> zeebeWorkerValues = new ArrayList<>();;
  private List<JobWorker> openedWorkers = new ArrayList<>();

  public ZeebeWorkerAnnotationProcessor(ReadZeebeWorkerValue reader, DefaultCommandExceptionHandlingStrategy commandExceptionHandlingStrategy, BackoffSupplier backoffSupplier) {
    this.reader = reader;
    this.commandExceptionHandlingStrategy = commandExceptionHandlingStrategy;
    this.backoffSupplier = backoffSupplier;
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public void configureFor(ClassInfo beanInfo) {
    List<ZeebeWorkerValue> newZeebeWorkerValues = new ArrayList<>();

    doWithMethods(
      beanInfo.getTargetClass(),
      method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(newZeebeWorkerValues::add),
      ReflectionUtils.USER_DECLARED_METHODS);

    beanName = beanInfo.getBeanName();
    LOGGER.info("Configuring {} Zeebe worker(s) of bean '{}': {}", newZeebeWorkerValues.size(), beanName, newZeebeWorkerValues);

    zeebeWorkerValues.addAll(newZeebeWorkerValues);
  }
  @Override
  public void start(ZeebeClient client) {
      zeebeWorkerValues.forEach(
        zeebeWorkerValue -> {
          final JobWorkerBuilderStep3 builder = client
            .newWorker()
            .jobType(zeebeWorkerValue.getType())
            .handler(new JobHandlerInvokingSpringBeans(zeebeWorkerValue, commandExceptionHandlingStrategy));

          if (zeebeWorkerValue.getName() != null && zeebeWorkerValue.getName().length() > 0) {
            // using name from annotation
            builder.name(zeebeWorkerValue.getName());
          } else if (null != client.getConfiguration().getDefaultJobWorkerName()) {
            // otherwise, default name from Spring config if set ([would be done automatically anyway])
            builder.name(client.getConfiguration().getDefaultJobWorkerName());
          } else {
            // otherwise, bean/method name combo
            builder.name(beanName + "#" + zeebeWorkerValue.getMethodInfo().getMethodName());
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

          JobWorker jobWorker = builder.open();
          openedWorkers.add(jobWorker);

          LOGGER.info(". Starting Zeebe worker: {}", zeebeWorkerValue);
        });
  }

  @Override
  public void stop(ZeebeClient zeebeClient) {
    openedWorkers.forEach( worker -> worker.close());
    openedWorkers = new ArrayList<>();
  }

}
