package io.camunda.zeebe.spring.client.annotation.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.bean.BeanInfo;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeDeploymentValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Always created by {@link AnnotationProcessorConfiguration}
 *
 * Loop throgh @{@link Deployment} annotations to deploy resources to Zeebe
 * once the {@link io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle} was initialized.
 */
public class ZeebeDeploymentAnnotationProcessor extends AbstractZeebeAnnotationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

  private List<ZeebeDeploymentValue> deploymentValues = new ArrayList<>();

  public ZeebeDeploymentAnnotationProcessor() {
  }

  @Override
  public boolean isApplicableFor(ClassInfo beanInfo) {
    return beanInfo.hasClassAnnotation(Deployment.class) || beanInfo.hasClassAnnotation(ZeebeDeployment.class);
  }

  @Override
  public void configureFor(final ClassInfo beanInfo) {
    Optional<ZeebeDeploymentValue> zeebeDeploymentValue = readAnnotation(beanInfo);
    if (zeebeDeploymentValue.isPresent()) {
      LOGGER.info("Configuring deployment: {}", zeebeDeploymentValue.get());
      deploymentValues.add(zeebeDeploymentValue.get());
    }
  }

  public Optional<ZeebeDeploymentValue> readAnnotation(ClassInfo beanInfo) {
    Optional<Deployment> annotation = beanInfo.getAnnotation(Deployment.class);
    if (!annotation.isPresent()) {
      return readDeprecatedAnnotation(beanInfo);
    } else {
      List<String> resources = Arrays.stream(annotation.get().resources()).collect(Collectors.toList());
      return Optional.of(ZeebeDeploymentValue.builder()
        .beanInfo(beanInfo)
        .resources(resources)
        .build());
    }
  }

  private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

  /**
   * Seperate handling for legacy annotation (had additional classPathResources attribute)
   * @param beanInfo
   */
  private Optional<ZeebeDeploymentValue> readDeprecatedAnnotation(ClassInfo beanInfo) {
    Optional<ZeebeDeployment> annotation = beanInfo.getAnnotation(ZeebeDeployment.class);
    if (!annotation.isPresent()) {
      return Optional.empty();
    }

    List<String> resources = Arrays.stream(annotation.get().resources()).collect(Collectors.toList());

    String[] classPathResources = annotation.get().classPathResources();
    if (classPathResources.length > 0) {
      resources.addAll(
        Arrays.stream(classPathResources)
          .map(resource -> CLASSPATH_ALL_URL_PREFIX + resource)
          .collect(Collectors.toList())
      );
    }

    return Optional.of(
      ZeebeDeploymentValue.builder()
        .beanInfo(beanInfo)
        .resources(resources)
        .build());
  }

  @Override
  public void start(final ZeebeClient client) {
    deploymentValues.forEach( deployment -> {

      DeployResourceCommandStep1 deployResourceCommand = client
        .newDeployResourceCommand();

      DeploymentEvent deploymentResult = deployment.getResources()
        .stream()
        .flatMap(resource -> Stream.of(getResources(resource)))
        .map(resource -> {
          try (InputStream inputStream = resource.getInputStream()) {
            return deployResourceCommand.addResourceStream(inputStream, resource.getFilename());
          } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
          }
        })
        .filter(Objects::nonNull)
        .reduce((first, second) -> second)
        .orElseThrow(() -> new IllegalArgumentException("Requires at least one resource to deploy"))
        .send()
        .join();

      LOGGER.info(
        "Deployed: {}",
        Stream.concat(deploymentResult
              .getDecisionRequirements()
              .stream()
              .map(wf -> String.format("<%s:%d>", wf.getDmnDecisionRequirementsId(), wf.getVersion())),
            deploymentResult
              .getProcesses()
              .stream()
              .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion())))
          .collect(Collectors.joining(",")));

    });
  }

  @Override
  public void stop(ZeebeClient client) {
    // noop for deployment
  }

  public Resource[] getResources(String resources) {
    try {
      return resourceResolver.getResources(resources);
    } catch (IOException e) {
      return new Resource[0];
    }
  }
}
