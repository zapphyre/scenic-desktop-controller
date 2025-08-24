package org.remote.desktop.mode.model;

import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

public abstract class Mode {

    public abstract ApplicationEvent currentModeEvent(GpadCommandEvent gEvt);
}
