package io.camunda.spring.client.config.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployProcessCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.spring.client.annotation.ZeebeDeployment;
import io.camunda.spring.client.bean.ClassInfo;
import io.camunda.spring.client.bean.value.ZeebeDeploymentValue;
import io.camunda.spring.client.bean.value.factory.ReadZeebeDeploymentValue;
import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentPostProcessor extends BeanInfoPostProcessor {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ReadZeebeDeploymentValue reader;

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

      DeployProcessCommandStep1 deployWorkflowCommand = client
        .newDeployCommand();

      DeploymentEvent deploymentResult = value.getClassPathResources()
        .stream()
        .map(deployWorkflowCommand::addResourceFromClasspath)
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
}
