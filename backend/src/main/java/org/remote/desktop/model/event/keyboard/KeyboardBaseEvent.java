package org.remote.desktop.model.event.keyboard;

import lombok.Getter;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEvent;

public abstract class KeyboardBaseEvent extends ApplicationEvent {

    @Getter
    protected final EActionButton button;

    public KeyboardBaseEvent(Object source, EActionButton button) {
        super(source);
        this.button = button;
    }
}
