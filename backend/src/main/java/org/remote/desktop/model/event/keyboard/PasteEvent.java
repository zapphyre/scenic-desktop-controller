package org.remote.desktop.model.event.keyboard;

import org.springframework.context.ApplicationEvent;

public class PasteEvent extends ApplicationEvent {

    public PasteEvent(Object source) {
        super(source);
    }
}
