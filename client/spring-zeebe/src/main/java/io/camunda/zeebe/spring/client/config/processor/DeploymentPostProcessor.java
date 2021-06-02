package io.camunda.zeebe.spring.client.config.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployProcessCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeDeploymentValue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class DeploymentPostProcessor extends BeanInfoPostProcessor {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ReadZeebeDeploymentValue reader;

  private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

  public DeploymentPostProcessor(ReadZeebeDeploymentValue reader) {
    this.reader = reader;
  }

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasClassAnnotation(ZeebeDeployment.class);
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    final ZeebeDeploymentValue value = reader.applyOrThrow(beanInfo);

    LOGGER.info("deployment: {}", value);

    return client -> {

      DeployProcessCommandStep1 deployProcessCommand = client
        .newDeployCommand();

      DeploymentEvent deploymentResult = value.getResources()
        .stream()
        .flatMap(resource -> Stream.of(getResources(resource)))
        .map(resource -> {
          try (InputStream inputStream = resource.getInputStream()) {
            return deployProcessCommand.addResourceStream(inputStream, resource.getFilename());
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
        deploymentResult
          .getProcesses()
          .stream()
          .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion()))
          .collect(Collectors.joining(",")));
    };
  }

  Resource[] getResources(String resources) {
    try {
      return resourceResolver.getResources(resources);
    } catch (IOException e) {
      return new Resource[0];
    }
  }
}
