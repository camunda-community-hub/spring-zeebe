package io.zeebe.spring.client.config;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Spring managed lifecycle implementation of {@link ZeebeClient}. Uses delegate for {@link
 * ZeebeClientImpl} internally.
 */
@Slf4j
public class SpringZeebeClient extends ZeebeAutoStartUpLifecycle<ZeebeClientImpl>
    implements ZeebeClient {

  private final ZeebeClientConfiguration properties;
  private final ApplicationEventPublisher publisher;

  /** Holds list of consumers to be notified after the client was started. */
  private final Set<Consumer<ZeebeClient>> startListener = new LinkedHashSet<>();

  private boolean hasBeenClosed = false;

  public SpringZeebeClient(
      final ZeebeClientConfiguration properties,
      final ApplicationEventPublisher publisher,
      final CreateDefaultTopic createDefaultTopic) {
    super(22222);
    this.properties = properties;
    this.publisher = publisher;

    addStartListener(createDefaultTopic);
    log.info("SpringZeebeClient created");
  }

  @Override
  public void onStart() {
    delegate = new ZeebeClientImpl(properties);
    log.info("SpringZeebeClient connected");
    publisher.publishEvent(new ClientStartedEvent());

    startListener.forEach(c -> c.accept(this));
  }

  @Override
  public void onStop() {
    close();
    log.info("SpringZeebeClient closed");
  }

  public SpringZeebeClient addStartListener(final Consumer<ZeebeClient> consumer) {
    startListener.add(consumer);
    return this;
  }

  @Override
  public TopicClient topicClient(final String topicName) {
    return get().topicClient();
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

  @Override
  public ZeebeClientConfiguration getConfiguration() {
    return get().getConfiguration();
  }

  @Override
  public void close() {
    if (!hasBeenClosed) {
      get().close();
      hasBeenClosed = true;
    }
  }
}
