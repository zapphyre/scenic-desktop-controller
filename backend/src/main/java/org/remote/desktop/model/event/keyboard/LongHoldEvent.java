package org.remote.desktop.model.event.keyboard;

import lombok.Value;
import org.remote.desktop.ui.model.EKeyboardInputButton;

@Value
public class LongHoldEvent extends KeyboardBaseEvent {

    public LongHoldEvent(Object source, EKeyboardInputButton button) {
        super(source, button);
    }
}
