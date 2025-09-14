package org.remote.desktop.model.event.keyboard;

import lombok.Getter;
import org.remote.desktop.ui.model.EKeyboardInputButton;
import org.springframework.context.ApplicationEvent;

public abstract class KeyboardBaseEvent extends ApplicationEvent {

    @Getter
    protected final EKeyboardInputButton button;

    public KeyboardBaseEvent(Object source, EKeyboardInputButton button) {
        super(source);
        this.button = button;
    }
}
