package org.remote.desktop.model.event.keyboard.impl;

import org.remote.desktop.model.EKeyEvt;

public class KeyUpEvent extends KeyboardEvent {

    public KeyUpEvent(Object source, EKeyEvt keyEvt, String keyPress) {
        super(source, keyEvt, keyPress);
    }
}
