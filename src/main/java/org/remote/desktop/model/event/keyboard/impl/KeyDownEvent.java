package org.remote.desktop.model.event.keyboard.impl;

import org.remote.desktop.model.EKeyEvt;

public class KeyDownEvent extends KeyboardEvent {

    public KeyDownEvent(Object source, EKeyEvt keyEvt, String keyPress) {
        super(source, keyEvt, keyPress);
    }
}
