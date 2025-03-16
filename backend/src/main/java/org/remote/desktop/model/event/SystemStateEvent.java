package org.remote.desktop.model.event;

import org.springframework.context.ApplicationEvent;

public abstract class SystemStateEvent extends ApplicationEvent {

    public SystemStateEvent(Object source) {
        super(source);
    }
}
