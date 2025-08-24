package org.remote.desktop.mode.model;

import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

public class WinderMode extends Mode {

    @Override
    public ApplicationEvent currentModeEvent(GpadCommandEvent gEvt) {
        return null;
    }
}
