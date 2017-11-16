package io.zeebe.spring.client.event;


import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher
{

    private final ApplicationEventPublisher publisher;

    public EventPublisher(final ApplicationEventPublisher publisher)
    {
        this.publisher = publisher;
    }


}
