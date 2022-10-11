package io.camunda.zeebe.spring.client.lifecycle;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.api.command.*;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1;
import io.camunda.zeebe.spring.client.event.ClientStartedEvent;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeAnnotationProcessorRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;

import java.util.function.Supplier;

/**
 * Lifecycle implementation that also directly acts as a ZeebeClient by delegating all methods to the
 * ZeebeClient that is controlled (and kept in the delegate field)
 *
 * Created by {@link io.camunda.zeebe.spring.client.ZeebeClientSpringConfiguration} (only for production code, not for tests)
 */
public class ZeebeClientLifecycle implements ZeebeClient, SmartLifecycle, Supplier<ZeebeClient> {

  public static final int PHASE = 22222;
  protected boolean autoStartup = true;
  protected boolean running = false;
  protected boolean runningInTestContext = false;

  private final ZeebeAnnotationProcessorRegistry annotationProcessorRegistry;
  private final ApplicationEventPublisher publisher;

  protected final ZeebeClientObjectFactory factory;
  protected ZeebeClient delegate;

  public ZeebeClientLifecycle(final ZeebeClientObjectFactory factory, final ZeebeAnnotationProcessorRegistry annotationProcessorRegistry, final ApplicationEventPublisher publisher) {
    this.factory = factory;
    this.annotationProcessorRegistry = annotationProcessorRegistry;
    this.publisher = publisher;
  }

  @Override
  public void start() {
    delegate = factory.getObject();
    if (delegate==null) {
      // in test cases the test support makes sure, this does not create a real client, which is OK for us here
      runningInTestContext = true;
    } else {
      this.running = true;
      publisher.publishEvent(new ClientStartedEvent());
      annotationProcessorRegistry.startAll(this);
    }
  }

  @Override
  public void stop() {
    try {
      delegate.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      running = false;
    }
    annotationProcessorRegistry.stopAll(this);
  }

  @Override
  public void close() {
    // needed to fulfill the ZeebeClient Interface
  }

  @Override
  public ZeebeClient get() {
    if (!isRunning()) {
      throw new IllegalStateException("ZeebeClient is not yet created!");
    }
    return delegate;
  }

  @Override
  public boolean isAutoStartup() {
    return autoStartup;
  }


  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public int getPhase() {
    return PHASE;
  }

  @Override
  public ZeebeClientConfiguration getConfiguration() {
    return get().getConfiguration();
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
  public DeployResourceCommandStep1 newDeployResourceCommand() {
    return get().newDeployResourceCommand();
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

  @Override
  public ModifyProcessInstanceCommandStep1 newModifyProcessInstanceCommand(long processInstanceKey) {
    return get().newModifyProcessInstanceCommand(processInstanceKey);
  }
}
