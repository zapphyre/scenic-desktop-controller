package org.remote.desktop.mode.model;

import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.XdoEvent;
import org.springframework.context.ApplicationEvent;

public class XdoMode extends Mode {

    @Override
    public ApplicationEvent currentModeEvent(GpadCommandEvent gEvt) {
        return new XdoEvent(this, gEvt.getKeyPart());
    }
}
