package org.remote.desktop.model.event;

import lombok.Value;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEvent;

@Value
public class ButtonEvent extends ApplicationEvent {

    EActionButton button;

    public ButtonEvent(Object source, EActionButton button) {
        super(source);
        this.button = button;
    }
}
