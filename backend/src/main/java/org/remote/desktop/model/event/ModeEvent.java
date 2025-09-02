package org.remote.desktop.model.event;

import lombok.Value;
import org.remote.desktop.mode.model.EMode;
import org.springframework.context.ApplicationEvent;

@Value
public class ModeEvent extends ApplicationEvent {

    EMode mode;

    public ModeEvent(Object source, EMode mode) {
        super(source);
        this.mode = mode;
    }
}
