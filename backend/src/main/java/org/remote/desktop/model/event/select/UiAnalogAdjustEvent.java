package org.remote.desktop.model.event.select;

import lombok.Value;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

@Value
public class UiAnalogAdjustEvent extends ApplicationEvent {

    GpadCommandEvent event;

    public UiAnalogAdjustEvent(Object source, GpadCommandEvent event) {
        super(source);
        this.event = event;
    }
}
