package io.zeebe.spring.client.event;

import io.zeebe.spring.client.ZeebeTemplate;

public class ClientStartedEvent {

    private final ZeebeTemplate zeebeTemplate;

    public ClientStartedEvent(ZeebeTemplate zeebeTemplate) {
        this.zeebeTemplate = zeebeTemplate;
    }

    public ZeebeTemplate getZeebeTemplate() {
        return zeebeTemplate;
    }
}
