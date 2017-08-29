package io.zeebe.spring.client;


import io.zeebe.client.event.TopicSubscription;
import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher {

    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }


}
