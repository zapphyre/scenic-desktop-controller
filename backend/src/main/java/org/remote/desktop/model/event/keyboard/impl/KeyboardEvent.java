package org.remote.desktop.model.event.keyboard.impl;

import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.SystemStateEvent;

public abstract class KeyboardEvent extends SystemStateEvent {
    protected final EKeyEvt keyEvt;
    protected final String keyPress;

    public KeyboardEvent(Object source, EKeyEvt keyEvt, String keyPress) {
        super(source);
        this.keyEvt = keyEvt;
        this.keyPress = keyPress;
    }
}
