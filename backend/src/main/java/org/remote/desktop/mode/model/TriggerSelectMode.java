package org.remote.desktop.mode.model;

import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.trigger.TriggerSelectEvent;
import org.springframework.context.ApplicationEvent;

public class TriggerSelectMode extends Mode {

    @Override
    public ApplicationEvent currentModeEvent(GpadCommandEvent gEvt) {
        return new TriggerSelectEvent(this);
    }
}
