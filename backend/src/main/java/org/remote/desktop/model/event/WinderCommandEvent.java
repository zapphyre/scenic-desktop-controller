package org.remote.desktop.model.event;

import lombok.Value;
import org.springframework.context.ApplicationEvent;
import org.winder.common.model.EWinderOp;

@Value
public class WinderCommandEvent extends ApplicationEvent {

    EWinderOp winderOp;

    public WinderCommandEvent(Object source, EWinderOp winderOp) {
        super(source);
        this.winderOp = winderOp;
    }
}
