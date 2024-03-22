package io.camunda.zeebe.spring.client.annotation.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * Always created by {@link AnnotationProcessorConfiguration}
 *
 * <p>Triggered by {@link ZeebeAnnotationProcessorRegistry#postProcessAfterInitialization(Object,
 * String)} to add Handler subscriptions for {@link ZeebeWorker} and {@link JobWorker}
 * method-annotations.
 */
public class ZeebeWorkerAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final JobWorkerManager jobWorkerManager;

  private final List<ZeebeWorkerValue> zeebeWorkerValues = new ArrayList<>();
  private final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers;

  public ZeebeWorkerAnnotationProcessor(
      final JobWorkerManager jobWorkerFactory,
      final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers) {
    this.jobWorkerManager = jobWorkerFactory;
    this.zeebeWorkerValueCustomizers = zeebeWorkerValueCustomizers;
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(JobWorker.class)
        || beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public void configureFor(ClassInfo beanInfo) {
    List<ZeebeWorkerValue> newZeebeWorkerValues = new ArrayList<>();

    doWithMethods(
        beanInfo.getTargetClass(),
        method ->
            readJobWorkerAnnotationForMethod(beanInfo.toMethodInfo(method))
                .ifPresent(newZeebeWorkerValues::add),
        ReflectionUtils.USER_DECLARED_METHODS);

    LOGGER.info(
        "Configuring {} Zeebe worker(s) of bean '{}': {}",
        newZeebeWorkerValues.size(),
        beanInfo.getBeanName(),
        newZeebeWorkerValues);
    zeebeWorkerValues.addAll(newZeebeWorkerValues);
  }

  public Optional<ZeebeWorkerValue> readJobWorkerAnnotationForMethod(final MethodInfo methodInfo) {
    Optional<JobWorker> methodAnnotation = methodInfo.getAnnotation(JobWorker.class);
    if (methodAnnotation.isPresent()) {
      JobWorker annotation = methodAnnotation.get();
      return Optional.of(
          new ZeebeWorkerValue(
              annotation.type(),
              annotation.name(),
              Duration.of(annotation.timeout(), ChronoUnit.MILLIS),
              annotation.maxJobsActive(),
              Duration.of(annotation.requestTimeout(), ChronoUnit.SECONDS),
              Duration.of(annotation.pollInterval(), ChronoUnit.MILLIS),
              annotation.autoComplete(),
              Arrays.asList(annotation.fetchVariables()),
              annotation.enabled(),
              methodInfo,
              Arrays.asList(annotation.tenantIds()),
              annotation.fetchAllVariables(),
              annotation.streamEnabled(),
              Duration.of(annotation.streamTimeout(), ChronoUnit.MILLIS)));
    } else {
      Optional<ZeebeWorker> legacyAnnotation = methodInfo.getAnnotation(ZeebeWorker.class);
      if (legacyAnnotation.isPresent()) {
        ZeebeWorker annotation = legacyAnnotation.get();
        return Optional.of(
            new ZeebeWorkerValue(
                annotation.type(),
                annotation.name(),
                Duration.of(annotation.timeout(), ChronoUnit.MILLIS),
                annotation.maxJobsActive(),
                Duration.of(annotation.requestTimeout(), ChronoUnit.SECONDS),
                Duration.of(annotation.pollInterval(), ChronoUnit.MILLIS),
                annotation.autoComplete(),
                Arrays.asList(annotation.fetchVariables()),
                annotation.enabled(),
                methodInfo,
                Arrays.asList(annotation.tenantIds()),
                annotation.forceFetchAllVariables(),
                false,
                Duration.ZERO));
      }
    }
    return Optional.empty();
  }

  @Override
  public void start(ZeebeClient client) {
    zeebeWorkerValues.stream()
        .peek(
            zeebeWorkerValue ->
                zeebeWorkerValueCustomizers.forEach(
                    customizer -> customizer.customize(zeebeWorkerValue)))
        .filter(ZeebeWorkerValue::getEnabled)
        .forEach(
            zeebeWorkerValue -> {
              jobWorkerManager.openWorker(client, zeebeWorkerValue);
            });
  }

  @Override
  public void stop(ZeebeClient zeebeClient) {
    jobWorkerManager.closeAllOpenWorkers();
  }
}
