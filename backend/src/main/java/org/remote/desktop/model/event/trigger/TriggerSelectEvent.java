package org.remote.desktop.model.event.trigger;

import lombok.Value;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

@Value
public class TriggerSelectEvent extends ApplicationEvent {

    boolean on;
    GpadCommandEvent event;

    public TriggerSelectEvent(Object source, boolean on, GpadCommandEvent event) {
        super(source);
        this.on = on;
        this.event = event;
    }
}
