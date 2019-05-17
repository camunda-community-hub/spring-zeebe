package io.zeebe.spring.client;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientConfiguration;
import io.zeebe.client.api.commands.ActivateJobsCommandStep1;
import io.zeebe.client.api.commands.CancelWorkflowInstanceCommandStep1;
import io.zeebe.client.api.commands.CompleteJobCommandStep1;
import io.zeebe.client.api.commands.CreateWorkflowInstanceCommandStep1;
import io.zeebe.client.api.commands.DeployWorkflowCommandStep1;
import io.zeebe.client.api.commands.FailJobCommandStep1;
import io.zeebe.client.api.commands.PublishMessageCommandStep1;
import io.zeebe.client.api.commands.ResolveIncidentCommandStep1;
import io.zeebe.client.api.commands.SetVariablesCommandStep1;
import io.zeebe.client.api.commands.TopologyRequestStep1;
import io.zeebe.client.api.commands.UpdateRetriesJobCommandStep1;
import io.zeebe.client.api.commands.WorkflowRequestStep1;
import io.zeebe.client.api.commands.WorkflowResourceRequestStep1;
import io.zeebe.client.api.subscription.JobWorkerBuilderStep1;
import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.client.event.ClientStartedEvent;
import io.zeebe.spring.util.ZeebeAutoStartUpLifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.context.ApplicationEventPublisher;

public class ZeebeClientLifecycle extends ZeebeAutoStartUpLifecycle<ZeebeClientImpl> implements
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
  public DeployWorkflowCommandStep1 newDeployCommand() {
    return get().newDeployCommand();
  }

  @Override
  public CreateWorkflowInstanceCommandStep1 newCreateInstanceCommand() {
    return get().newCreateInstanceCommand();
  }

  @Override
  public CancelWorkflowInstanceCommandStep1 newCancelInstanceCommand(long workflowInstanceKey) {
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
  public WorkflowResourceRequestStep1 newResourceRequest() {
    return get().newResourceRequest();
  }

  @Override
  public WorkflowRequestStep1 newWorkflowRequest() {
    return get().newWorkflowRequest();
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
  public FailJobCommandStep1 newFailCommand(long jobKey) {
    return get().newFailCommand(jobKey);
  }
}
