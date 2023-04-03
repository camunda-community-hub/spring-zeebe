package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.ReflectionUtils.doWithMethods;

/**
 * Always created by {@link AnnotationProcessorConfiguration}
 *
 * Triggered by {@link ZeebeAnnotationProcessorRegistry#postProcessAfterInitialization(Object, String)} to add Handler subscriptions for {@link ZeebeWorker}
 * method-annotations.
 */
public class ZeebeWorkerAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final JobWorkerManager jobWorkerManager;

  private final List<ZeebeWorkerValue> zeebeWorkerValues = new ArrayList<>();
  private final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers;
  private String defaultWorkerType;
  private String defaultWorkerName;

  public ZeebeWorkerAnnotationProcessor(final JobWorkerManager jobWorkerFactory,
                                        final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers,
                                        final String defaultWorkerType, final String defaultJobWorkerName) {
    this.jobWorkerManager = jobWorkerFactory;
    this.zeebeWorkerValueCustomizers = zeebeWorkerValueCustomizers;
    this.defaultWorkerType = defaultWorkerType;
    this.defaultWorkerName = defaultJobWorkerName;
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(JobWorker.class) || beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public void configureFor(ClassInfo beanInfo) {
    List<ZeebeWorkerValue> newZeebeWorkerValues = new ArrayList<>();

    doWithMethods(
      beanInfo.getTargetClass(),
      method -> readJobWorkerAnnotationForMethod(beanInfo.toMethodInfo(method)).ifPresent(newZeebeWorkerValues::add),
      ReflectionUtils.USER_DECLARED_METHODS);

    LOGGER.info("Configuring {} Zeebe worker(s) of bean '{}': {}", newZeebeWorkerValues.size(), beanInfo.getBeanName(), newZeebeWorkerValues);
    zeebeWorkerValues.addAll(newZeebeWorkerValues);
  }

  public Optional<ZeebeWorkerValue> readJobWorkerAnnotationForMethod(final MethodInfo methodInfo) {
    Optional<JobWorker> methodAnnotation = methodInfo.getAnnotation(JobWorker.class);
    if (methodAnnotation.isPresent()) {
      JobWorker annotation = methodAnnotation.get();
      ZeebeWorkerValue workerValue = new ZeebeWorkerValue();
      workerValue.setMethodInfo(methodInfo);
      workerValue.setType(annotation.type());
      workerValue.setTimeout(annotation.timeout());
      workerValue.setMaxJobsActive(annotation.maxJobsActive());
      workerValue.setPollInterval(annotation.pollInterval());
      workerValue.setAutoComplete(annotation.autoComplete());
      workerValue.setRequestTimeout(annotation.requestTimeout());
      workerValue.setEnabled(annotation.enabled());

    // TODO Get rid of those initialize methods but add the attributes as values onto the worker and then auto-initialize stuff when opening the worker
      workerValue.initializeName(annotation.name(), methodInfo, defaultWorkerName);
      workerValue.initializeFetchVariables(annotation.fetchAllVariables(), annotation.fetchVariables(), methodInfo);
      workerValue.initializeJobType(annotation.type(), methodInfo, defaultWorkerType);
      return Optional.of(workerValue);
    } else {
      Optional<ZeebeWorker> legacyAnnotation = methodInfo.getAnnotation(ZeebeWorker.class);
      if (legacyAnnotation.isPresent()) {
        ZeebeWorker annotation = legacyAnnotation.get();

        ZeebeWorkerValue workerValue = new ZeebeWorkerValue();
        workerValue.setMethodInfo(methodInfo);
        workerValue.setType(annotation.type());
        workerValue.setTimeout(annotation.timeout());
        workerValue.setMaxJobsActive(annotation.maxJobsActive());
        workerValue.setPollInterval(annotation.pollInterval());
        workerValue.setAutoComplete(annotation.autoComplete());
        workerValue.setRequestTimeout(annotation.requestTimeout());
        workerValue.setEnabled(annotation.enabled());

        // TODO Get rid of those initialize methods but add the attributes as values onto the worker and then auto-initialize stuff when opening the worker
        workerValue.initializeName(annotation.name(), methodInfo, defaultWorkerName);
        workerValue.initializeFetchVariables(annotation.forceFetchAllVariables(), annotation.fetchVariables(), methodInfo);
        workerValue.initializeJobType(annotation.type(), methodInfo, defaultWorkerType);
        return Optional.of(workerValue);
      }
    }
    return Optional.empty();
  }

  @Override
  public void start(ZeebeClient client) {
    zeebeWorkerValues
      .stream()
      .peek(zeebeWorkerValue -> zeebeWorkerValueCustomizers.forEach(customizer -> customizer.customize(zeebeWorkerValue)))
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
