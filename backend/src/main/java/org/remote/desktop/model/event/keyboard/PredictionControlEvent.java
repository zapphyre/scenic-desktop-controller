package org.remote.desktop.model.event.keyboard;

import lombok.Value;
import org.asmus.model.ELogicalEventType;
import org.remote.desktop.ui.model.EActionButton;

@Value
public class PredictionControlEvent extends KeyboardBaseEvent {

    ELogicalEventType logical;
    String type;

    public PredictionControlEvent(Object source, EActionButton button, ELogicalEventType logical, String type) {
        super(source, button);
        this.logical = logical;
        this.type = type;
    }
}
