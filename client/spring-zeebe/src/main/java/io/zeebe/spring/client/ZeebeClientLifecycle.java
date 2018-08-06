package io.zeebe.spring.client;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientConfiguration;
import io.zeebe.client.api.clients.TopicClient;
import io.zeebe.client.api.commands.CreateTopicCommandStep1;
import io.zeebe.client.api.commands.TopicsRequestStep1;
import io.zeebe.client.api.commands.TopologyRequestStep1;
import io.zeebe.client.api.record.ZeebeObjectMapper;
import io.zeebe.client.api.subscription.ManagementSubscriptionBuilderStep1;
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
  public TopicClient topicClient(final String topicName) {
    return get().topicClient(topicName);
  }

  @Override
  public TopicClient topicClient() {
    return get().topicClient();
  }

  @Override
  public ZeebeObjectMapper objectMapper() {
    return get().objectMapper();
  }

  @Override
  public CreateTopicCommandStep1 newCreateTopicCommand() {
    return get().newCreateTopicCommand();
  }

  @Override
  public TopicsRequestStep1 newTopicsRequest() {
    return get().newTopicsRequest();
  }

  @Override
  public TopologyRequestStep1 newTopologyRequest() {
    return get().newTopologyRequest();
  }

  @Override
  public ManagementSubscriptionBuilderStep1 newManagementSubscription() {
    return get().newManagementSubscription();
  }
}
