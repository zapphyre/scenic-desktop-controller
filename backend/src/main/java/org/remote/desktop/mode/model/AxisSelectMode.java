package org.remote.desktop.mode.model;

import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.select.AxisSelectEvent;
import org.springframework.context.ApplicationEvent;

public class AxisSelectMode extends Mode {

    private final XdoMode xdoMode = new XdoMode();

    @Override
    public ApplicationEvent currentModeEvent(GpadCommandEvent gEvt) {
        if (gEvt.getKeyPart().getKeyEvt() == EKeyEvt.TRIGGER_ADJUST)
            return new AxisSelectEvent(this, true, gEvt);
//        else
//            return new TriggerSelectEvent(this, false, gEvt);

        return xdoMode.currentModeEvent(gEvt);
    }
}
