package org.remote.desktop.model.event;

import lombok.Value;
import org.springframework.context.ApplicationEvent;

@Value
public class WinderCommandEvent extends ApplicationEvent {

    public WinderCommandEvent(Object source) {
        super(source);
    }
}
