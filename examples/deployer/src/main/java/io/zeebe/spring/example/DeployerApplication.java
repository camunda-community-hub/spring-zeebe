package io.zeebe.spring.example;

import io.zeebe.client.WorkflowsClient;
import io.zeebe.client.event.DeploymentEvent;
import io.zeebe.spring.client.annotation.EnableZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableZeebeClient
public class DeployerApplication implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(DeployerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DeployerApplication.class, args);
    }

    @Autowired
    private WorkflowsClient workflows;

    @Override
    public void run(String... strings) throws Exception {
        final InputStream bpmnStream = DeployerApplication.class.getResourceAsStream("/demoProcess.bpmn");
        DeploymentEvent deploymentResult = workflows
                .deploy("default-topic")
                .resourceStream(bpmnStream)
                .execute();

        log.info("Deployed: {}",
                deploymentResult.getDeployedWorkflows().stream()
                        .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion()))
                        .collect(Collectors.joining(",")));

    }
}
