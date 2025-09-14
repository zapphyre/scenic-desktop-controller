package org.remote.desktop.model.event.keyboard;

import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;
import org.remote.desktop.ui.model.EKeyboardInputButton;

import java.util.Set;

@Value
public class ButtonEvent extends KeyboardBaseEvent {

    EQualificationType qualification;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;

    public ButtonEvent(Object source, EKeyboardInputButton button, EQualificationType qualification, Set<EButtonAxisMapping> modifiers, boolean longPress) {
        super(source, button);
        this.qualification = qualification;
        this.modifiers = modifiers;
        this.longPress = longPress;
    }
}
