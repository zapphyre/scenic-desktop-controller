package org.remote.desktop.model.event;

import lombok.Value;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Value
public class LongHoldEvent extends ApplicationEvent {

    EActionButton button;

    public LongHoldEvent(Object source, EActionButton button) {
        super(source);
        this.button = button;
    }

    public LongHoldEvent(Object source, Clock clock, EActionButton button) {
        super(source, clock);
        this.button = button;
    }
}
