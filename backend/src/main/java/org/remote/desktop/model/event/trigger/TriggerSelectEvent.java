package org.remote.desktop.model.event.trigger;

import org.springframework.context.ApplicationEvent;

public class TriggerSelectEvent extends ApplicationEvent {

    public TriggerSelectEvent(Object source) {
        super(source);
    }
}
