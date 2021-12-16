package io.camunda.zeebe.spring.client;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.command.*;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1;
import io.camunda.zeebe.spring.client.event.ClientStartedEvent;
import io.camunda.zeebe.spring.util.ZeebeAutoStartUpLifecycle;
import org.springframework.context.ApplicationEventPublisher;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ZeebeClientLifecycle extends ZeebeAutoStartUpLifecycle<ZeebeClient> implements
  ZeebeClient {

  public static final int PHASE = 22222;

  private final ApplicationEventPublisher publisher;
  private final Set<Consumer<ZeebeClient>> startListener = new LinkedHashSet<>();

  public ZeebeClientLifecycle(final ZeebeClientObjectFactory factory,
    final ApplicationEventPublisher publisher) {
    super(PHASE, factory);
    this.publisher = publisher;
  }

  public ZeebeClientLifecycle addStartListener(final Consumer<ZeebeClient> consumer) {
    startListener.add(consumer);
    if (isRunning()) {
      // In test cases the call sequence seems to be different, still need to understand why, but this fixes it
      consumer.accept(this);
    }
    return this;
  }

  @Override
  public void start() {
    super.start();

    publisher.publishEvent(new ClientStartedEvent());

    startListener.forEach(c -> c.accept(this));
  }

  @Override
  public ZeebeClientConfiguration getConfiguration() {
    return get().getConfiguration();
  }

  @Override
  public void close() {
    // needed to fulfill the ZeebeClient Interface
    stop();
  }

  @Override
  public TopologyRequestStep1 newTopologyRequest() {
    return get().newTopologyRequest();
  }

  @Override
  public DeployProcessCommandStep1 newDeployCommand() {
    return get().newDeployCommand();
  }

  @Override
  public CreateProcessInstanceCommandStep1 newCreateInstanceCommand() {
    return get().newCreateInstanceCommand();
  }

  @Override
  public CancelProcessInstanceCommandStep1 newCancelInstanceCommand(long workflowInstanceKey) {
    return get().newCancelInstanceCommand(workflowInstanceKey);
  }

  @Override
  public SetVariablesCommandStep1 newSetVariablesCommand(long elementInstanceKey) {
    return get().newSetVariablesCommand(elementInstanceKey);
  }

  @Override
  public PublishMessageCommandStep1 newPublishMessageCommand() {
    return get().newPublishMessageCommand();
  }

  @Override
  public ResolveIncidentCommandStep1 newResolveIncidentCommand(long incidentKey) {
    return get().newResolveIncidentCommand(incidentKey);
  }

  @Override
  public UpdateRetriesJobCommandStep1 newUpdateRetriesCommand(long jobKey) {
    return get().newUpdateRetriesCommand(jobKey);
  }

  @Override
  public UpdateRetriesJobCommandStep1 newUpdateRetriesCommand(ActivatedJob job) {
    return newUpdateRetriesCommand(job.getKey());
  }

  @Override
  public JobWorkerBuilderStep1 newWorker() {
    return get().newWorker();
  }

  @Override
  public ActivateJobsCommandStep1 newActivateJobsCommand() {
    return get().newActivateJobsCommand();
  }

  @Override
  public CompleteJobCommandStep1 newCompleteCommand(long jobKey) {
    return get().newCompleteCommand(jobKey);
  }

  @Override
  public CompleteJobCommandStep1 newCompleteCommand(ActivatedJob job) {
    return newCompleteCommand(job.getKey());
  }

  @Override
  public FailJobCommandStep1 newFailCommand(long jobKey) {
    return get().newFailCommand(jobKey);
  }

  @Override
  public FailJobCommandStep1 newFailCommand(ActivatedJob job) {
    return newFailCommand(job.getKey());
  }

  @Override
  public ThrowErrorCommandStep1 newThrowErrorCommand(long jobKey) {
    return get().newThrowErrorCommand(jobKey);
  }

  @Override
  public ThrowErrorCommandStep1 newThrowErrorCommand(ActivatedJob job) {
    return newThrowErrorCommand(job.getKey());
  }

}
