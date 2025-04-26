package org.remote.desktop.model.event.keyboard;

import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.ELogicalEventType;
import org.remote.desktop.ui.model.EActionButton;

import java.util.Set;

@Value
public class PredictionControlEvent extends KeyboardBaseEvent {

    ELogicalEventType logical;
    String type;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;

    public PredictionControlEvent(Object source, EActionButton button, ELogicalEventType logical, String type, Set<EButtonAxisMapping> modifiers, boolean longPress) {
        super(source, button);
        this.logical = logical;
        this.type = type;
        this.modifiers = modifiers;
        this.longPress = longPress;
    }
}
