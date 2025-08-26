package org.remote.desktop.mode.model;

import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.keyboard.KeyboardWidgetControlEvent;
import org.remote.desktop.model.event.keyboard.LongHoldEvent;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEvent;

public class KeyboardMode extends Mode {

    @Override
    public ApplicationEvent currentModeEvent(GpadCommandEvent e) {
        if (e.getKeyPart().getKeyEvt() == EKeyEvt.KEYBOARD_LONG)
            return new LongHoldEvent(this, EActionButton.valueOf(e.getTrigger()));
        else if (e.getKeyPart().getKeyEvt() == EKeyEvt.KEYBOARD_ON) {
            return new KeyboardWidgetControlEvent(this, true);
        } else if (e.getKeyPart().getKeyEvt() == EKeyEvt.KEYBOARD_OFF)
            return new KeyboardWidgetControlEvent(this, false);
        else
            return new PredictionControlEvent(this, null, null, e.getTrigger(), e.getModifiers(), e.isLongPress());
    }
}
