package io.zeebe.spring.client.config.processor;

import io.zeebe.client.event.DeploymentEvent;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class DeploymentPostProcessor extends BeanInfoPostProcessor {

    @Override
    public boolean test(final ClassInfo beanInfo) {
        return beanInfo.hasClassAnnotation(ZeebeDeployment.class);
    }

    @Override
    public Consumer<SpringZeebeClient> apply(final ClassInfo beanInfo) {
        final ZeebeDeployment.Annotated annotated = ZeebeDeployment.Annotated.of(beanInfo);

        log.info("deployment: {}", annotated);

        return client -> {
            final DeploymentEvent deploymentResult = client.workflows()
                    .deploy(annotated.getTopicName())
                    .resourceFromClasspath(annotated.getClassPathResource())
                    .execute();

            log.info("Deployed: {}",
                    deploymentResult.getDeployedWorkflows().stream()
                            .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion()))
                            .collect(Collectors.joining(",")));

        };
    }
}
