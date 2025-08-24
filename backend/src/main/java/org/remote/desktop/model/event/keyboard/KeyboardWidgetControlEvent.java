package org.remote.desktop.model.event.keyboard;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class KeyboardWidgetControlEvent extends ApplicationEvent {

    @Getter
    private final boolean on;

    public KeyboardWidgetControlEvent(Object source, boolean on) {
        super(source);
        this.on = on;
    }
}
