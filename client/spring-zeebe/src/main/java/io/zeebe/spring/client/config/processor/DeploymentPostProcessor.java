package io.zeebe.spring.client.config.processor;

import io.zeebe.client.event.DeploymentEvent;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeDeploymentValue;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class DeploymentPostProcessor extends BeanInfoPostProcessor {

    private final ReadZeebeDeploymentValue reader;

    public DeploymentPostProcessor(ReadZeebeDeploymentValue reader) {
        this.reader = reader;
    }

    @Override
    public boolean test(final ClassInfo beanInfo) {
        return beanInfo.hasClassAnnotation(ZeebeDeployment.class);
    }

    @Override
    public Consumer<SpringZeebeClient> apply(final ClassInfo beanInfo) {
        final ZeebeDeploymentValue value = reader.applyOrThrow(beanInfo);

        log.info("deployment: {}", value);

        return client -> {
            final DeploymentEvent deploymentResult = client.workflows()
                    .deploy(value.getTopicName())
                    .resourceFromClasspath(value.getClassPathResource())
                    .execute();

            log.info("Deployed: {}",
                    deploymentResult.getDeployedWorkflows().stream()
                            .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion()))
                            .collect(Collectors.joining(",")));

        };
    }

}
