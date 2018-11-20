package io.zeebe.spring.client.config.processor;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.events.DeploymentEvent;
import io.zeebe.spring.api.SpringZeebeApiKt;
import io.zeebe.spring.api.command.CreateDeployment;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeDeploymentValue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeploymentPostProcessor extends BeanInfoPostProcessor {

  private final ReadZeebeDeploymentValue reader;

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasClassAnnotation(ZeebeDeployment.class);
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    final ZeebeDeploymentValue value = reader.applyOrThrow(beanInfo);

    log.info("deployment: {}", value);

    return client -> {
      final DeploymentEvent deploymentResult = SpringZeebeApiKt
        .apply(client, new CreateDeployment(value.getClassPathResource()))
        .join();

      log.info(
        "Deployed: {}",
        deploymentResult
          .getWorkflows()
          .stream()
          .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion()))
          .collect(Collectors.joining(",")));
    };
  }
}
