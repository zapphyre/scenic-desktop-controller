package org.remote.desktop.model.event.keyboard;

import lombok.Value;
import org.asmus.model.EQualificationType;
import org.remote.desktop.ui.model.EActionButton;

@Value
public class ButtonEvent extends KeyboardBaseEvent {

    EQualificationType qualification;

    public ButtonEvent(Object source, EActionButton button, EQualificationType qualification) {
        super(source, button);
        this.qualification = qualification;
    }
}
