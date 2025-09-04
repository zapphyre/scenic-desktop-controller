package org.remote.desktop.model.event;

import lombok.Value;
import org.remote.desktop.mode.model.EMode;
import org.springframework.context.ApplicationEvent;

@Value
public class ModeEvent extends ApplicationEvent {

    String mode;

    public ModeEvent(Object source, String mode) {
        super(source);
        this.mode = mode;
    }
}
