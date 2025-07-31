package org.remote.desktop.model.event;

import org.springframework.context.ApplicationEvent;

public class NoopCommandEvent extends ApplicationEvent {
    public NoopCommandEvent(Object source) {
        super(source);
    }
}
