package org.remote.desktop.model.event;

import org.springframework.context.ApplicationEvent;

public class GestureCommandEvent extends ApplicationEvent {


    public GestureCommandEvent(Object source) {
        super(source);
    }
}
