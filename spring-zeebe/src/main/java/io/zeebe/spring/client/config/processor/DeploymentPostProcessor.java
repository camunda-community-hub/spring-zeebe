package io.zeebe.spring.client.config.processor;

import io.zeebe.client.event.DeploymentEvent;
import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.bean.BeanInfo;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.ZeebeDeploymentValue;
import io.zeebe.spring.client.config.SpringZeebeClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class DeploymentPostProcessor extends BeanInfoPostProcessor<ClassInfo, ZeebeDeployment, ZeebeDeploymentValue> {

    @Override
    public boolean test(final ClassInfo beanInfo) {
        return beanInfo.hasClassAnnotation(ZeebeDeployment.class);
    }

    @Override
    public Consumer<SpringZeebeClient> apply(final ClassInfo beanInfo) {
        final ZeebeDeploymentValue value = create(beanInfo).orElseThrow(BeanInfo.noAnnotationFound(annotationType()));

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

    @Override
    public Class<ZeebeDeployment> annotationType() {
        return ZeebeDeployment.class;
    }

    @Override
    public Optional<ZeebeDeploymentValue> create(final ClassInfo beanInfo) {
        return beanInfo.getAnnotation(annotationType())
                .map(annotation -> ZeebeDeploymentValue.builder()
                        .beanInfo(beanInfo)
                        .topicName(resolver.resolve(annotation.topicName()))
                        .classPathResource(resolver.resolve(annotation.classPathResource()))
                        .build());
    }

}
