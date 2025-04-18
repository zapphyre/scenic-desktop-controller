package org.remote.desktop.model.event.keyboard;

import lombok.Value;
import org.remote.desktop.ui.model.EActionButton;

@Value
public class LongHoldEvent extends KeyboardBaseEvent {

    public LongHoldEvent(Object source, EActionButton button) {
        super(source, button);
    }
}
