package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.annotation.value.factory.ReadZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.jobhandling.JobWorkerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
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
  private final JobWorkerManager jobWorkerManager;

  private String beanName = null;
  private final List<ZeebeWorkerValue> zeebeWorkerValues = new ArrayList<>();
  private final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers;

  public ZeebeWorkerAnnotationProcessor(final ReadZeebeWorkerValue reader,
                                        final JobWorkerManager jobWorkerFactory,
                                        final List<ZeebeWorkerValueCustomizer> zeebeWorkerValueCustomizers) {
    this.reader = reader;
    this.jobWorkerManager = jobWorkerFactory;
    this.zeebeWorkerValueCustomizers = zeebeWorkerValueCustomizers;
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
