package org.remote.desktop.ui.trigger;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.remote.desktop.model.EAxisEvent;

@Value
@RequiredArgsConstructor
public class TriggerSelectable implements UiSelectable<EAxisEvent> {

    EAxisEvent event;

    @Override
    public EAxisEvent getSectableItem() {
        return event;
    }

    @Override
    public String getItemName() {
        return event.name();
    }
}
