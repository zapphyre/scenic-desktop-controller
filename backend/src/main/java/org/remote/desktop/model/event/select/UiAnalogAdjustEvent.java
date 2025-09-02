package org.remote.desktop.model.event.select;

import lombok.Value;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

@Value
public class UiAnalogAdjustEvent extends ApplicationEvent {

    boolean on;
    GpadCommandEvent event;

    public UiAnalogAdjustEvent(Object source, boolean on, GpadCommandEvent event) {
        super(source);
        this.on = on;
        this.event = event;
    }
}
